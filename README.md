# ToDo Application - Full-Stack Task Management

This is a full-stack Trello-like task management application built as part of the Software Engineer Intern technical assignment. It features a task board with three status columns (To Do, Doing, Done) and drag-and-drop functionality.

## Features
- **User Roles:** Normal Users and Administrators (seeded).
- **Authentication:** JWT-based secure authentication.
- **Task Management:** Create tasks, assign to users, change statuses via drag-and-drop.
- **Role-Based Access:** 
  - Normal users can manage their own tasks and assign unassigned tasks to themselves.
  - Admins have complete control over all tasks and can assign them to anyone.
- **Premium UI:** Modern glassmorphism aesthetic with responsive design.

## Technology Stack
- **Frontend:** Next.js (App Router), React, Vanilla CSS
- **Backend:** Java Spring Boot, Spring Security
- **Database:** MongoDB
- **Security:** JSON Web Tokens (JWT), BCrypt Password Hashing

## Prerequisites
- Java 21 or later
- Node.js 18 or later
- MongoDB (Running locally on port 27017 or a MongoDB Atlas URI)

## Setup Instructions

### Environment Variables
#### Backend (`backend/src/main/resources/application.properties`)
```properties
# MongoDB URI (default is localhost)
# Set SPRING_DATA_MONGODB_URI in your environment if using Atlas
spring.data.mongodb.uri=${SPRING_DATA_MONGODB_URI:mongodb://localhost:27017/todoapp}

# JWT Secret (Must be base64 encoded string at least 256 bits)
app.jwt.secret=${JWT_SECRET:thisisasecretkeywhichshouldbeatleast256bitslongsothatitworks}
app.jwt.expirationMs=${JWT_EXPIRATION_MS:86400000}
```

#### Frontend (`frontend/src/lib/api.js`)
Ensure the `API_URL` points to your backend instance.
```javascript
export const API_URL = 'http://localhost:8080/api';
```

### Running the Backend
1. Navigate to the `backend` directory.
2. Run the application using the Maven wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```
   *Note: On Windows use `.\mvnw spring-boot:run`*
3. The server will start on `http://localhost:8080`.
4. **Admin Account:** The seeder will automatically create an admin user on the first run.
   - Username: `admin`
   - Password: `admin123`

### Running the Frontend
1. Navigate to the `frontend` directory.
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the development server:
   ```bash
   npm run dev
   ```
4. Open `http://localhost:3000` in your browser.

## Deployment Information
- **Frontend:** Can be easily deployed on Vercel or Netlify. Connect the GitHub repo and set the build command to `npm run build`.
- **Backend:** Can be deployed on platforms like Render, Heroku, or AWS Elastic Beanstalk. Ensure to set the `SPRING_DATA_MONGODB_URI` environment variable.
- **Database:** MongoDB Atlas is recommended for production databases.

## Screenshots

*(Candidate: Insert Application Screenshots Here)*

- Login Page
- Dashboard (Normal User View)
- Dashboard (Admin View)
- Drag and Drop functionality
