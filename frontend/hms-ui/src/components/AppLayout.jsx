import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { unreadCount } from "../api/notifications";
import "../styles/app.css";
import "../styles/billing.css";

/**
 * Shared page chrome (top bar + content area) for the authenticated app.
 * Pages pass a title and their own content as children.
 */
function AppLayout({ title, actions, children }) {
  const navigate = useNavigate();
  const role = localStorage.getItem("role");
  const name = localStorage.getItem("name");
  const [unread, setUnread] = useState(0);

  useEffect(() => {
    // Best-effort: the bell shouldn't break a page if the service is down.
    unreadCount().then(setUnread).catch(() => setUnread(0));
  }, []);

  const handleLogout = () => {
    localStorage.clear();
    window.location.href = "/login";
  };

  return (
    <div className="app-container">
      <nav className="app-navbar">
        <div className="app-brand" onClick={() => navigate("/dashboard")}>
          <span className="app-logo">HMS</span>
          <div>
            <h1>{title}</h1>
            <p className="app-subtitle">
              {name ? `${name} · ` : ""}
              <span className="app-role">{role}</span>
            </p>
          </div>
        </div>

        <div className="app-navbar-actions">
          {actions}
          <button className="bell" onClick={() => navigate("/notifications")} title="Notifications">
            🔔
            {unread > 0 && <span className="count">{unread}</span>}
          </button>
          <button className="btn btn-ghost" onClick={() => navigate("/dashboard")}>
            Dashboard
          </button>
          <button className="btn btn-danger" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </nav>

      <main className="app-content">{children}</main>
    </div>
  );
}

export default AppLayout;
