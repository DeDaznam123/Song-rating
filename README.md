<p align="center">
  <img src="https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg" alt="Review Assignment Due Date">
</p>

<h1 align="center">Group Assignment 2IRR00 2025 – Group 49</h1>

## 🎵 Project name: Fentify

**Fentify** is a music reviewing desktop application that allows users to search for songs, rate and review them, and receive personalized music recommendations based on their reviews.

### 👥 Group Members

**Victor Handzhiev (2138549)**  
  - Git Username: `DeDaznam123`
  - Git Email: 'victorhandzhiev@gmail.com'

**Stoyan Meshov (2159384)**  
  - Git Username: `Kexium`  
  - Git Email: 'stoianmeshov@gmail.com'

**Miguel Federico Lebrun Ceño (2111152)**  
  - Git Username: `@xacobeu`  
  - Git Email: 'miguelartlebrun@gmail.com'

**David Mandado Loureiro (1978276)**
  - Git Username: `@DavidMandado`  
  - Git Email: 'd.mandado@student.tue.nl'

**Marko Spyrou (2106426)**  
  - Git Username: `@SpyrouMarko`  
  - Git Email: 'm.a.spyrou@student.tue.nl'

**Kiril Aleksiev (2108399)**  
  - Git Username: `@SamuraiK999`  
  - Git Email: 'kialexiev@gmail.com'

## 📄 Use Case Reference

The updated, executable use case scenario (including at least two alternative or exception paths) can be found in the following file:

`UseCase.md`

## 🧩 Design Patterns Used

The following design patterns were applied in the implementation of **Fentify**:

- **Model-View-Controller (MVC):**  
  The application architecture follows the MVC pattern.  
  - *Model:* Application data such as songs, reviews, and users.  
  - *View:* JavaFX components rendering the UI (e.g., song detail view, review form).  
  - *Controller:* Handles user interaction logic and updates the view or model accordingly.
 
- **Other folders include:**
  - *Services:* Mocked APIs.
  - *Utils:* Utility classes for theme, database, etc.
  - *Test:* Test classes.

- **Observer Pattern:**  
  Used through runnable interfaces and consumers to implement MVC and communicate from the Controllers to the views.

- **Singleton Pattern:**
  Used for the Database and User/Session manager. Very logical application of the pattern as these should only have 1 instance.

- **Factory method:**
  Used to instantiate the User object upon a succesful log in or sign in.

If any patterns were deemed overcomplicated for simpler features or didn't offer much in terms of extensibility, we typically opted for straightforward design over forced abstraction.

## Implemented algorithms

The following are some of the important algorithms used in the development of **Fentify**:

- **User rating similarity**
  The User rating similarity function is found in the Rating Recommender Service, where it serves as a modified version
  of the popular cosine similarity algorithm, allowing us to get a value for the similarity between users likes

- **The Rating Recommender algorithm**
  Using the user similarity algorithm, as well as a rating matrix constructed from the database, this algorithm takes in a user id and integer value n
  which are used to predict ratings of reviewables the specified user hasn't rated based on how other users have rated that reviewable. Which is where the similarity
  value comes in as it ensures users closer to the target have their ratings weighted more. Finally this uses a sorting algorithm to get the n highest rated reviewables
  and return them.

- **The Comment-based Recommendation algorithm**
  This service recommends 10 reviewable items based on all their reviews using a TF-IDF algorithm. The CommentRecommenderService builds a vocabulary and computes IDF (inverse document frequency) scores from all reviews. Each item's text data is processed in parallel via CustomThread, which updates the TF (term frequency) and TF-IDF maps concurrently.  When a recommendation is requested, it compares the TF-IDF vectors of the items (songs, artists, albums) liked by the current user with the TF-IDF vectors of all other items via cosine similarity. The algorithm ranks and filters suggestions by similarity score, providing contextually relevant comment recommendations to user.
