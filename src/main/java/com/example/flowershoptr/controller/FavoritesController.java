package com.example.flowershoptr.controller;

import com.example.flowershoptr.model.Flower;
import com.example.flowershoptr.service.FavoritesService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Контроллер для работы с избранным
 */
@RequiredArgsConstructor
@Controller
@RequestMapping("/favorites")
public class FavoritesController {

    private final FavoritesService favoritesService;



    /**
     * Отображает страницу избранных товаров
     */
    @GetMapping
    public String viewFavorites(Model model, HttpSession session) {
        List<Flower> items = favoritesService.getFavoritesItems(session);
        model.addAttribute("favoritesItems", items);
        model.addAttribute("itemsCount", items.size());

        return "client/favorites/view";
    }

    /**
     * API для добавления товара в избранное
     */
    @PostMapping("/api/add")
    @ResponseBody
    public ResponseEntity<?> addToFavorites(@RequestParam("flowerId") Long flowerId,
                                            HttpSession session) {
        favoritesService.addToFavorites(flowerId, session);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("totalItems", favoritesService.getFavoritesCount(session));
        response.put("inFavorites", true);

        return ResponseEntity.ok(response);
    }

    /**
     * API для удаления товара из избранного
     */
    @PostMapping("/api/remove")
    @ResponseBody
    public ResponseEntity<?> removeFromFavorites(@RequestParam("flowerId") Long flowerId,
                                                 HttpSession session) {
        favoritesService.removeFromFavorites(flowerId, session);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("totalItems", favoritesService.getFavoritesCount(session));
        response.put("inFavorites", false);

        return ResponseEntity.ok(response);
    }

    /**
     * API для проверки, находится ли товар в избранном
     */
    @GetMapping("/api/check")
    @ResponseBody
    public ResponseEntity<?> checkInFavorites(@RequestParam("flowerId") Long flowerId,
                                              HttpSession session) {
        boolean isInFavorites = favoritesService.isInFavorites(flowerId, session);

        Map<String, Object> response = new HashMap<>();
        response.put("inFavorites", isInFavorites);

        return ResponseEntity.ok(response);
    }

    /**
     * API для очистки избранного
     */
    @PostMapping("/api/clear")
    @ResponseBody
    public ResponseEntity<?> clearFavorites(HttpSession session) {
        favoritesService.clearFavorites(session);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("totalItems", 0);

        return ResponseEntity.ok(response);
    }

    /**
     * API для получения количества товаров в избранном
     */
    @GetMapping("/api/count")
    @ResponseBody
    public ResponseEntity<?> getFavoritesCount(HttpSession session) {
        int count = favoritesService.getFavoritesCount(session);

        Map<String, Object> response = new HashMap<>();
        response.put("count", count);

        return ResponseEntity.ok(response);
    }

    /**
     * API для переключения состояния товара в избранном
     * (добавляет, если его нет; удаляет, если есть)
     */
    @PostMapping("/api/toggle")
    @ResponseBody
    public ResponseEntity<?> toggleFavorite(@RequestParam("flowerId") Long flowerId,
                                            HttpSession session) {
        boolean isInFavorites = favoritesService.isInFavorites(flowerId, session);

        if (isInFavorites) {
            favoritesService.removeFromFavorites(flowerId, session);
        } else {
            favoritesService.addToFavorites(flowerId, session);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("totalItems", favoritesService.getFavoritesCount(session));
        response.put("inFavorites", !isInFavorites);

        return ResponseEntity.ok(response);
    }
}