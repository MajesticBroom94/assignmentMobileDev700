# Smart Pantry Manager

Smart Pantry Manager is an Android application developed in Java for the Mobile App Development 700 assignment.

The application allows users to manage their pantry ingredients and find recipes they can prepare using the ingredients they already have.

## Features

- **Pantry Management:** Add, view, edit and delete ingredients, including quantities, measurement units and optional expiry dates.
- **Recipe Suggestions:** Display recipes that can be prepared using the ingredients currently available in the pantry.
- **Strict Recipe Matching:** A recipe is only suggested under *Can Make* when all required ingredients are available in sufficient quantities.
- **Almost There:** Displays recipes that are missing exactly one ingredient.
- **Recipe Details:** View the required ingredients and preparation steps.
- **Recipe Completion:** Mark a recipe as completed to deduct the ingredients used from the pantry.
- **Expiry Warnings:** Highlight ingredients that have expired or will expire within seven days. Warnings can be enabled or disabled in Settings.
- **Data Persistence:** Pantry ingredients and recipes are stored in Firebase Cloud Firestore.

## Database

The application uses **Firebase Cloud Firestore**, a NoSQL document database.

Each user's pantry ingredients are stored under their Firebase Authentication user ID:

`users/{userId}/pantryItems/{itemId}`

Recipes are stored in a shared collection:

`recipes/{recipeId}`

Firestore was selected because it provides cloud-based storage and integrates directly with Android applications without requiring a separate backend API.

The application uses anonymous Firebase authentication to identify users and keep their pantry records separate.

## Running the Application

1. Clone or download the repository.
2. Open the project in Android Studio.
3. Allow Gradle to synchronise and download the required dependencies.
4. Make sure an Android emulator or physical Android device is available.
5. Run the application using Android Studio.

The project includes the Firebase client configuration in `app/google-services.json`.

An active internet connection is required for Firebase authentication and database operations.

## How to Use

1. Open the **Pantry** screen and add ingredients with their quantities and measurement units.
2. Open **Suggested Recipes** to view recipes under 'Can Make' and 'Almost There'.
3. Select a recipe to view its ingredients and preparation instructions.
4. For recipes under Can Make, use **I Made This** to deduct the required quantities from the pantry.
5. Use **Settings** to enable or disable expiry-date warnings.

## Recipe Data

The application includes 20 predefined recipes, which are automatically added to Firebase Firestore if they do not already exist.