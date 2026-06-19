"""
================================================================
  Mini-Client Python Interactif — Projet 5 XML (ENSIAS)
  Technologies XML & Services Web | Prof. Najat Chadli | 2024/25
================================================================

Point d'entrée du mini-client.

Rôle :
    Afficher le menu principal et rediriger vers les sous-menus
    dédiés à chaque ressource (User, Product, Task) ainsi que
    vers le menu de tests de validation XML.

Lancement :
    python main.py

Prérequis :
    - Python 3.6+
    - Bibliothèque requests  : pip install requests
    - L'API Spring Boot doit tourner sur http://localhost:8080
================================================================
"""

from menus.user_menu       import user_menu
from menus.product_menu    import product_menu
from menus.task_menu       import task_menu
from menus.validation_menu import validation_menu
from utils.colors          import Colors

# ── URL de base de l'API (modifiable selon l'environnement) ──────────────────
BASE_URL = "http://localhost:8080"


def print_banner():
    """Affiche la bannière d'accueil du mini-client."""
    print(Colors.BLUE_BOLD)
    print("╔══════════════════════════════════════════════════════╗")
    print("║        API REST – Mini-Client Python Interactif      ║")
    print("║   ENSIAS – Technologies XML & Services Web 2024/25   ║")
    print("╚══════════════════════════════════════════════════════╝")
    print(Colors.RESET)
    print(f"  Serveur cible  : {Colors.CYAN}{BASE_URL}{Colors.RESET}")
    print("  Formats supportés : JSON  |  XML (XSD)  |  XML (DTD)")
    print()


def print_main_menu():
    """Affiche le menu principal."""
    print(Colors.BLUE_BOLD + "══════════════ MENU PRINCIPAL ══════════════" + Colors.RESET)
    print("  1 → Gérer les Utilisateurs  (User)")
    print("  2 → Gérer les Produits      (Product)")
    print("  3 → Gérer les Tâches        (Task)")
    print("  4 → Tests de Validation XML (XSD / DTD)")
    print("  0 → Quitter")
    print(Colors.BLUE_BOLD + "────────────────────────────────────────────" + Colors.RESET)


def main():
    print_banner()

    while True:
        print_main_menu()
        choix = input("Votre choix : ").strip()

        if   choix == "1": user_menu(BASE_URL)
        elif choix == "2": product_menu(BASE_URL)
        elif choix == "3": task_menu(BASE_URL)
        elif choix == "4": validation_menu(BASE_URL)
        elif choix == "0":
            print(Colors.CYAN + "\n👋  Au revoir !\n" + Colors.RESET)
            break
        else:
            print(Colors.YELLOW + "⚠  Choix invalide. Veuillez réessayer." + Colors.RESET)


if __name__ == "__main__":
    main()
