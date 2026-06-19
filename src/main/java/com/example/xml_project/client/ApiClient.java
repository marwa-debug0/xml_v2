package com.example.xml_project.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

/**
 * Client simple pour tester l'API REST.
 */
public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080/api";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== CLIENT MINI-API REST XML / JSON ===");
        boolean continuer = true;

        while (continuer) {
            System.out.println("\n--- MENU ---");
            System.out.println("1. Lister les utilisateurs (GET JSON)");
            System.out.println("2. Créer un utilisateur (POST JSON)");
            System.out.println("3. Créer un utilisateur avec validation XSD (POST XML)");
            System.out.println("4. Lister les produits (GET JSON)");
            System.out.println("5. Créer un produit avec validation DTD (POST XML)");
            System.out.println("6. Lister les tâches (GET JSON)");
            System.out.println("0. Quitter");
            System.out.print("Votre choix : ");

            String choix = scanner.nextLine();
            switch (choix) {
                case "1" -> getRequest("/users");
                case "2" -> createUserJson();
                case "3" -> createUserXml();
                case "4" -> getRequest("/products");
                case "5" -> createProductXml();
                case "6" -> getRequest("/tasks");
                case "0" -> {
                    continuer = false;
                    System.out.println("Au revoir !");
                }
                default -> System.out.println("Choix invalide !");
            }
        }
    }

    private static void getRequest(String path) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + path))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Code Statut : " + response.statusCode());
            System.out.println("Réponse : " + response.body());
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    private static void createUserJson() {
        System.out.print("Nom : ");
        String name = scanner.nextLine();
        System.out.print("Email : ");
        String email = scanner.nextLine();
        System.out.print("Mot de passe : ");
        String password = scanner.nextLine();

        String body = String.format("{\"name\":\"%s\",\"email\":\"%s\",\"password\":\"%s\"}", name, email, password);
        postRequest("/users", body, "application/json");
    }

    private static void createUserXml() {
        System.out.print("Nom : ");
        String name = scanner.nextLine();
        System.out.print("Email : ");
        String email = scanner.nextLine();
        System.out.print("Mot de passe : ");
        String password = scanner.nextLine();

        String body = String.format(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<user>\n" +
                "    <name>%s</name>\n" +
                "    <email>%s</email>\n" +
                "    <password>%s</password>\n" +
                "</user>", name, email, password);

        postRequest("/users/xml", body, "application/xml");
    }

    private static void createProductXml() {
        System.out.print("Nom du produit : ");
        String name = scanner.nextLine();
        System.out.print("Prix : ");
        String price = scanner.nextLine();

        String body = String.format(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE product SYSTEM \"product.dtd\">\n" +
                "<product>\n" +
                "    <name>%s</name>\n" +
                "    <price>%s</price>\n" +
                "</product>", name, price);

        postRequest("/products/xml", body, "application/xml");
    }

    private static void postRequest(String path, String body, String contentType) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + path))
                    .header("Content-Type", contentType)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Code Statut : " + response.statusCode());
            System.out.println("Réponse : " + response.body());
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}
