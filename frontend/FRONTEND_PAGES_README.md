# Pages Frontend - Système de Gestion des Déchets

## 📋 Vue d'ensemble

Le frontend comprend maintenant 3 pages principales avec des fonctionnalités spécifiques selon le rôle de l'utilisateur.

## 🏠 Page d'Accueil (Home)

**Route:** `/` ou `/home`  
**Accessible:** Tous les utilisateurs

### Fonctionnalités:
- Présentation du système 7achwa
- Boutons d'action dynamiques selon le rôle:
  - **Administrateurs:** Lien vers le tableau de bord admin
  - **Citoyens/Employés:** Lien vers l'espace citoyen
- Carte de statut de l'utilisateur avec affichage du rôle
- Cartes de fonctionnalités principales
- Design moderne avec animations

### Composants:
- `home.component.ts` - Logique de détection de rôle
- `home.component.html` - Template avec design Hero et CTA
- `home.component.css` - Styles modernes avec gradients

---

## ⚙️ Tableau de Bord Admin

**Route:** `/dashboard`  
**Accessible:** Administrateurs uniquement (rôle `admin`)  
**Protection:** `AuthGuard`

### Fonctionnalités:
- **Vue d'ensemble:** Statistiques en temps réel
  - Total utilisateurs (actifs/inactifs)
  - Véhicules disponibles/opérationnels
  - Conteneurs fonctionnels
  - Points de collecte
  - Routes planifiées

- **Navigation par onglets:**
  1. **Vue d'ensemble:** Stats agrégées et actions rapides
  2. **Utilisateurs:** Table complète avec rôles, statuts, auth faciale
  3. **Véhicules:** Cartes avec matricule, type, capacité, statut
  4. **Conteneurs:** Cartes avec type, capacité, niveau de remplissage
  5. **Routes:** Liste des routes avec dates et horaires
  6. **Points de Collecte:** Cartes avec localisation et coordonnées

### Services utilisés:
- `UserService`
- `VehiculeService`
- `ContainerService`
- `RouteService`
- `PickUpPointService`

### Design:
- Gradient violet/bleu moderne
- Cartes statistiques colorées (primary, success, warning, info, danger)
- Tables responsives pour les données
- Cartes d'entités avec hover effects
- Système d'onglets pour la navigation

---

## 🏠 Espace Citoyen

**Route:** `/citizen`  
**Accessible:** Citoyens et Employés (rôles `user`, `employe`)  
**Protection:** `AuthGuard`

### Fonctionnalités:

#### 1. **Statistiques Rapides**
- Nombre de points de collecte
- Nombre de conteneurs disponibles
- Nombre de prochaines collectes

#### 2. **Points de Collecte Proches**
- Liste des points de collecte avec adresses
- Conteneurs disponibles par point
- Statut des conteneurs (fonctionnel/non fonctionnel)
- Icônes par type de déchet (♻️ Plastique, 📦 Carton)

#### 3. **Prochaines Collectes**
- Calendrier des routes de collecte
- Heures de début et fin
- Statut de la collecte

#### 4. **Informations et Guide de Tri**
- Informations importantes sur la collecte
- Guide de tri des déchets
- Instructions pour les citoyens

### Services utilisés:
- `PickUpPointService`
- `ContainerService`
- `RouteService`

### Design:
- Gradient violet/bleu moderne
- Cartes informatives avec icônes
- Design responsive mobile-first
- États de chargement et erreurs
- Badges de statut colorés

---

## 🔧 Services Frontend

### Nouveaux services créés:

#### `VehiculeService`
```typescript
- getAll(): Observable<AppResponse>
- getById(id: string)
- getByMatricul(matricul: string)
- create(vehicule: Partial<Vehicule>)
- update(id: string, vehicule: Partial<Vehicule>)
- delete(id: string)
```

#### `ContainerService`
```typescript
- getAll(): Observable<AppResponse>
- getById(id: string)
- getByPickUpPointId(pickUpPointId: string)
- getByStatus(status: ContainerStatus)
- create(container: Partial<Container>)
- update(id: string, container: Partial<Container>)
- delete(id: string)
```

#### `PickUpPointService`
```typescript
- getAll(): Observable<AppResponse>
- getById(id: string)
- getByLocation(location: string)
- create(pickUpPoint: Partial<PickUpPoint>)
- update(id: string, pickUpPoint: Partial<PickUpPoint>)
- delete(id: string)
```

#### `RouteService`
```typescript
- getAll(): Observable<AppResponse>
- getById(id: string)
- getByDate(date: string)
- getByDateRange(startDate: string, endDate: string)
- create(route: Partial<Route>)
- update(id: string, route: Partial<Route>)
- delete(id: string)
```

---

## 📦 Modèles et Enums

### Enums (synchronisés avec le backend):

#### `VehiculeType`
- `Camion`
- `Car`

#### `VehiculeStatus`
- `Fonctionnel`
- `En_maintenance`

#### `ContainerType`
- `Plastique`
- `Carton`

#### `ContainerStatus`
- `functional`
- `non_functional`

### Modèles d'entités:

#### `Vehicule`
```typescript
{
  id?: string;
  matricul: string;
  type: VehiculeType;
  capacity: number;
  status: VehiculeStatus;
  users?: string[];
  createdAt?: string;
  updatedAt?: string;
}
```

#### `Container`
```typescript
{
  id?: string;
  type: ContainerType;
  capacity: number;
  fillLevel: number;
  containerStatus: ContainerStatus;
  pickUpPointId: string;
  createdAt?: string;
  updatedAt?: string;
}
```

#### `PickUpPoint`
```typescript
{
  id?: string;
  location: string;
  containers?: string[];
  latitude?: number;
  longitude?: number;
  address?: string;
  createdAt?: string;
  updatedAt?: string;
}
```

#### `Route`
```typescript
{
  id?: string;
  pickUpPoints?: string[];
  vehicule?: string;
  users?: string[];
  routeDate: string;
  startTime?: string;
  endTime?: string;
  status?: string;
  createdAt?: string;
  updatedAt?: string;
}
```

### Modèles supplémentaires:

#### `DashboardStats`
Statistiques agrégées pour le tableau de bord admin

#### `CollectionRequest`
Requêtes de collecte des citoyens

#### `Notification`
Notifications utilisateur

---

## 🎨 Thème et Design

### Palette de couleurs:
- **Primary:** Gradient violet/bleu (`#667eea` → `#764ba2`)
- **Success:** Vert (`#48bb78`)
- **Warning:** Orange (`#ed8936`)
- **Danger:** Rouge (`#f56565`)
- **Info:** Bleu (`#4299e1`)

### Caractéristiques du design:
- Gradients modernes
- Cartes avec ombres et hover effects
- Animations fluides
- Design responsive
- Icônes emoji pour la lisibilité
- Badges colorés pour les statuts

---

## 🚀 Routes configurées

```typescript
{
  path: '',
  component: MainLayoutComponent,
  children: [
    { path: '', component: HomeComponent },
    { path: 'home', component: HomeComponent },
    { 
      path: 'dashboard', 
      component: DashboardComponent, 
      data: { roles: ['admin'] }, 
      canActivate: [AuthGuard] 
    },
    { 
      path: 'citizen', 
      component: CitizenComponent, 
      data: { roles: ['user', 'employe'] }, 
      canActivate: [AuthGuard] 
    },
    { 
      path: 'profile', 
      component: ProfileComponent, 
      data: { roles: ['user'] } 
    }
  ]
}
```

---

## 🔐 Sécurité

- Toutes les routes protégées utilisent `AuthGuard`
- Vérification des rôles via Keycloak
- Redirection automatique selon le rôle
- Protection des endpoints API côté backend

---

## 📱 Responsive Design

Toutes les pages sont entièrement responsives avec:
- Grid adaptative
- Breakpoints pour mobile/tablet/desktop
- Menus hamburger sur mobile
- Tables scrollables horizontalement
- Cartes empilées sur petits écrans

---

## 🧪 Tests

Chaque composant a son fichier de test:
- `home.component.spec.ts`
- `dashboard.component.spec.ts`
- `citizen.component.spec.ts`

---

## 📝 Notes de développement

1. Les enums frontend sont **exactement synchronisés** avec le backend Java
2. Tous les services suivent le pattern Observable/RxJS
3. Les composants utilisent la syntaxe Angular 18 moderne (`@if`, `@for`)
4. CommonModule importé pour les directives Angular
5. RouterModule importé pour la navigation
6. Gestion des erreurs et états de chargement sur toutes les pages

---

## 🎯 Prochaines étapes suggérées

- [ ] Ajouter des modals pour créer/éditer les entités
- [ ] Implémenter la recherche et les filtres
- [ ] Ajouter des graphiques (Chart.js ou NGX-Charts)
- [ ] Implémenter la pagination pour les grandes listes
- [ ] Ajouter des notifications toast
- [ ] Intégrer une carte interactive pour les points de collecte
- [ ] Ajouter l'export PDF/Excel des données
