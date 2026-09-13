git config user.email "lesstaxihr@gmail.com"
git config user.name "Osada"
git remote add origin https://github.com/osadaRajapaksha/ToDoApplication.git

git add "Software Engineer Intern Technical Assignment v1- lesstaxi.docx"
git commit -m "Initial commit: Add assignment requirements document"

git add backend/pom.xml backend/mvnw backend/mvnw.cmd backend/.mvn
git commit -m "Initialize Spring Boot backend skeleton"

git add backend/src/main/resources/application.properties
git commit -m "Configure MongoDB and JWT in application.properties"

git add backend/src/main/java/com/lesstaxi/todoapp/TodoappApplication.java
git commit -m "Add main Spring Boot application class"

git add backend/src/main/java/com/lesstaxi/todoapp/models/ERole.java backend/src/main/java/com/lesstaxi/todoapp/models/EStatus.java
git commit -m "Add ERole and EStatus enums"

git add backend/src/main/java/com/lesstaxi/todoapp/models/User.java
git commit -m "Create User domain model"

git add backend/src/main/java/com/lesstaxi/todoapp/models/Task.java
git commit -m "Create Task domain model"

git add backend/src/main/java/com/lesstaxi/todoapp/repositories/
git commit -m "Implement UserRepository and TaskRepository"

git add backend/src/main/java/com/lesstaxi/todoapp/security/jwt/JwtUtils.java
git commit -m "Add JwtUtils for JWT generation and validation"

git add backend/src/main/java/com/lesstaxi/todoapp/security/jwt/AuthEntryPointJwt.java backend/src/main/java/com/lesstaxi/todoapp/security/jwt/AuthTokenFilter.java
git commit -m "Implement JWT entry point and token filter"

git add backend/src/main/java/com/lesstaxi/todoapp/security/services/
git commit -m "Add UserDetails implementation for security"

git add backend/src/main/java/com/lesstaxi/todoapp/security/WebSecurityConfig.java
git commit -m "Configure WebSecurityConfig for role-based access"

git add backend/src/main/java/com/lesstaxi/todoapp/payload/
git commit -m "Add payload DTOs for requests and responses"

git add backend/src/main/java/com/lesstaxi/todoapp/controllers/AuthController.java
git commit -m "Implement AuthController for login and registration"

git add backend/src/main/java/com/lesstaxi/todoapp/controllers/TaskController.java
git commit -m "Implement TaskController for CRUD and assignments"

git add backend/src/main/java/com/lesstaxi/todoapp/controllers/UserController.java backend/src/main/java/com/lesstaxi/todoapp/config/AdminSeeder.java
git commit -m "Add UserController and AdminSeeder"

git add frontend/package.json frontend/package-lock.json frontend/next.config.mjs frontend/jsconfig.json frontend/.eslintrc.json frontend/.gitignore
git commit -m "Initialize Next.js frontend skeleton"

git add frontend/src/app/globals.css
git commit -m "Setup global CSS design system with glassmorphism"

git add frontend/src/lib/api.js frontend/src/context/AuthContext.js
git commit -m "Implement API client and Auth context"

git add frontend/src/app/layout.js frontend/src/app/page.js
git commit -m "Configure root layout and redirects"

git add frontend/src/app/login/ frontend/src/app/register/
git commit -m "Implement Login and Register pages"

git add frontend/src/app/dashboard/ frontend/src/components/TaskBoard.js
git commit -m "Build Dashboard and Drag-and-Drop TaskBoard"

git add README.md
git commit -m "Add comprehensive README documentation"

git add .
git commit -m "Final polish and clean up"
