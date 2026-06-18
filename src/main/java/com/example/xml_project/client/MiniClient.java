package com.example.xml_project.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

/**
 * Interactive Java mini-client for the REST API.
 *
 * This is a standalone console program (its own main method) that consumes the
 * running API over HTTP. Start the Spring Boot application first, then run this
 * class separately:
 *
 *   mvn spring-boot:run                          (terminal 1 - starts the API)
 *   java -cp target/classes com.example.xml_project.client.MiniClient   (terminal 2)
 *
 * It lets you list/create users and products as JSON or XML.
 */
public class MiniClient {

    private static final String BASE_URL = "http://localhost:8080/api";
    private final HttpClient http = HttpClient.newHttpClient();
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new MiniClient().run();
    }

    private void run() {
        System.out.println("=== Mini-client REST (xml_project) ===");
        System.out.println("API : " + BASE_URL);
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> listUsers();
                    case "2" -> createUserJson();
                    case "3" -> createUserXml();
                    case "4" -> listProducts();
                    case "5" -> createProductJson();
                    case "0" -> running = false;
                    default -> System.out.println("Choix invalide.");
                }
            } catch (Exception e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        }
        System.out.println("Au revoir !");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("1) Lister les utilisateurs (JSON)");
        System.out.println("2) Créer un utilisateur (JSON)");
        System.out.println("3) Créer un utilisateur (XML + validation XSD)");
        System.out.println("4) Lister les produits (JSON)");
        System.out.println("5) Créer un produit (JSON)");
        System.out.println("0) Quitter");
        System.out.print("> ");
    }

    private void listUsers() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users"))
                .header("Accept", "application/json")
                .GET()
                .build();
        send(req);
    }

    private void createUserJson() throws Exception {
        System.out.print("Nom : ");
        String name = scanner.nextLine().trim();
        System.out.print("Email : ");
        String email = scanner.nextLine().trim();
        System.out.print("Mot de passe : ");
        String password = scanner.nextLine().trim();

        String json = String.format(
                "{\"name\":\"%s\",\"email\":\"%s\",\"password\":\"%s\"}",
                name, email, password);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users"))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        send(req);
    }

    private void createUserXml() throws Exception {
        System.out.print("Nom : ");
        String name = scanner.nextLine().trim();
        System.out.print("Email : ");
        String email = scanner.nextLine().trim();
        System.out.print("Mot de passe : ");
        String password = scanner.nextLine().trim();

        String xml = String.format(
                "<user><name>%s</name><email>%s</email><password>%s</password></user>",
                name, email, password);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/xml"))
                .header("Content-Type", "application/xml")
                .header("Accept", "application/xml")
                .POST(HttpRequest.BodyPublishers.ofString(xml))
                .build();
        send(req);
    }

    private void listProducts() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/products"))
                .header("Accept", "application/json")
                .GET()
                .build();
        send(req);
    }

    private void createProductJson() throws Exception {
        System.out.print("Nom du produit : ");
        String name = scanner.nextLine().trim();
        System.out.print("Prix : ");
        String price = scanner.nextLine().trim();

        String json = String.format("{\"name\":\"%s\",\"price\":%s}", name, price);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/products"))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        send(req);
    }

    private void send(HttpRequest req) throws Exception {
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        System.out.println("--- Réponse (HTTP " + res.statusCode() + ") ---");
        System.out.println(res.body());
    }
}
