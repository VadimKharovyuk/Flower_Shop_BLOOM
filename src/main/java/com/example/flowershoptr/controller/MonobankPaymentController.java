package com.example.flowershoptr.controller;

import com.example.flowershoptr.dto.Order.CreateOrderDTO;
import com.example.flowershoptr.dto.cart.CartDto;
import com.example.flowershoptr.model.Order;
import com.example.flowershoptr.model.Payment;
import com.example.flowershoptr.service.CartService;
import com.example.flowershoptr.service.OrderService;
import com.example.flowershoptr.service.payment.MonobankPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Controller
@RequestMapping("/payment/monobank")
@RequiredArgsConstructor
public class MonobankPaymentController {

    private final MonobankPaymentService monobankPaymentService;
    private final OrderService orderService;
    private final CartService cartService;
    @Value("${monobank.api.token}")
    private String apiToken;

    @GetMapping("/process/{orderId}")
    public String processMonobankPayment(@PathVariable Long orderId, Model model, HttpServletRequest request) {
        try {
            Order order = orderService.getOrderById(orderId);

            if (order == null) {
                throw new Exception("Заказ не найден");
            }

            // Получаем текущий базовый URL
            String baseUrl = request.getScheme() + "://" + request.getServerName();
            if (request.getServerPort() != 80 && request.getServerPort() != 443) {
                baseUrl += ":" + request.getServerPort();
            }

            // Указываем куда Monobank должен редиректить после оплаты
            String returnUrl = baseUrl + "/payment/monobank/success";
            String webHookUrl = baseUrl + "/api/monobank/webhook";

            // Создаём платёж
            Payment payment = monobankPaymentService.processPayment(order, returnUrl, webHookUrl);

            // Получаем ссылку на оплату из API Monobank
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest statusRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.monobank.ua/api/merchant/invoice/status?invoiceId=" + payment.getTransactionId()))
                    .header("X-Token", apiToken)
                    .GET()
                    .build();

            HttpResponse<String> statusResponse = client.send(statusRequest, HttpResponse.BodyHandlers.ofString());
            String responseBody = statusResponse.body();
            System.out.println("Monobank API response: " + responseBody); // Логируем ответ

            JSONObject statusJson = new JSONObject(responseBody);

            // Проверяем структуру JSON и наличие ключа pageUrl
            if (statusJson.has("pageUrl")) {
                String paymentUrl = statusJson.getString("pageUrl");
                model.addAttribute("paymentLink", paymentUrl);
            } else {
                // Возможно, URL платежа хранится в другом месте в JSON
                // Пробуем из логики createPayment, если там была сохранена ссылка
                // Или можно использовать прямую ссылку на основе ID
                String paymentUrl = "https://pay.monobank.ua/" + payment.getTransactionId();
                model.addAttribute("paymentLink", paymentUrl);

                System.out.println("Using fallback payment URL: " + paymentUrl);
            }

            model.addAttribute("order", order);
            return "client/payment/monobank";
        } catch (Exception e) {
            e.printStackTrace(); // Печатаем стек вызовов для отладки
            model.addAttribute("errorMessage", "Ошибка при процессе оплаты Monobank: " + e.getMessage());
            model.addAttribute("orderDTO", new CreateOrderDTO());
            CartDto cart = cartService.getCartDto(request.getSession());
            model.addAttribute("item", cart);
            return "client/checkout/form";
        }
    }


    @GetMapping("/success")
    public String paymentSuccess(@RequestParam(value = "payment_id", required = false) String paymentId,
                                 @RequestParam(value = "data", required = false) String data,
                                 HttpSession session,
                                 Model model) {
        try {
            if (paymentId != null && data != null) {
                // Обработка данных, полученных от Monobank (или другого платежного сервиса)
                Payment payment = monobankPaymentService.completePayment(paymentId, data);

                if (payment != null) {
                    // Очищаем корзину после успешной оплаты
                    cartService.clearCart(session);
                    return "redirect:/checkout/confirmation/" + payment.getOrder().getId() + "?payment_status=success";
                }
            }

            model.addAttribute("errorMessage", "Не удалось найти информацию о платеже");
            return "client/payment/error";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Ошибка при обработке оплаты: " + e.getMessage());
            return "client/payment/error";  // Страница ошибки
        }
    }

    // Страница отмены
    @GetMapping("/cancel")
    public String paymentCancel(Model model) {
        model.addAttribute("message", "Оплата была отменена. Вы можете попробовать снова.");
        return "client/checkout/form";  // Страница отмены
    }
}