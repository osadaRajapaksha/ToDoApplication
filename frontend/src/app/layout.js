import "./globals.css";
import { AuthProvider } from "../context/AuthContext";

export const metadata = {
  title: "ToDo Application",
  description: "A premium Trello-like task management app",
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
