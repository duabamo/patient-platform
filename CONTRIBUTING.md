# Contributing

Merci de contribuer à ce projet ! Voici les conventions à respecter.

## Workflow Git

- Branche principale protégée : `main`
- Branche de développement : `develop`
- Branches de fonctionnalité : `feature/<nom-court>` (ex: `feature/add-pathology-export`)
- Branches de correctif : `fix/<nom-court>`

## Commits

Utiliser le format **Conventional Commits** :

```
<type>(<scope>): <description courte>

<corps optionnel>
```

Types courants : `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`.

Exemples :
- `feat(patient-profile): add search by birth date range`
- `fix(reporting): handle null pathologies in global report`
- `docs(readme): update docker-compose instructions`

## Pull Requests

1. Créer une branche depuis `develop`
2. Ouvrir une PR vers `develop`
3. Remplir le template de PR
4. Attendre la validation CI (GitHub Actions)
5. Au moins 1 review d'un coéquipier

## Style de code

- Java 17
- Indentation : 4 espaces (pas de tabs)
- Pas d'imports avec `*`
- Lombok autorisé (`@Getter`, `@Setter`, `@Builder`, `@RequiredArgsConstructor`)
- Tous les commits doivent passer `mvn test`

## Tests

- Tout nouveau service doit avoir au moins 1 test unitaire
- Les bug fixes doivent inclure un test de non-régression
