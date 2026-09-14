import "./globals.css";
import { AuthProvider } from "../context/AuthContext";

export const metadata = {
  title: "TaskFlow – ToDo Application",
  description: "A premium Trello-like task management app | Deployed via CI/CD",
};

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body>
        <AuthProvider>
          {children}
        </AuthProvider>
      </body>
    </html>
  );
}
