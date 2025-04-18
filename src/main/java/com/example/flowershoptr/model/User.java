package com.example.flowershoptr.model;


import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String name;
    private String googleId;
    private String pictureUrl;

    // Дополнительные поля, которые вы можете захотеть добавить
    private String role = "USER"; // Роль по умолчанию
    private boolean enabled = true;

    // Конструкторы, геттеры и сеттеры если не используете Lombok
}