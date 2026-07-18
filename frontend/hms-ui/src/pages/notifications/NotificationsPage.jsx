import { useEffect, useState } from "react";
import AppLayout from "../../components/AppLayout";
import { listNotifications, markAllRead, markRead } from "../../api/notifications";
import "../../styles/billing.css";

function NotificationsPage() {
  const [notifications, setNotifications] = useState([]);
  const [unreadOnly, setUnreadOnly] = useState(false);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  const load = async (unread) => {
    setLoading(true);
    setError("");
    try {
      setNotifications(await listNotifications(unread));
    } catch (e) {
      setError(e.response?.data?.message || "Failed to load notifications");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load(false);
  }, []);

  const handleRead = async (n) => {
    if (n.read) return;
    try {
      await markRead(n.id);
      load(unreadOnly);
    } catch {
      /* non-critical */
    }
  };

  const handleReadAll = async () => {
    await markAllRead().catch(() => {});
    load(unreadOnly);
  };

  return (
    <AppLayout
      title="Notifications"
      actions={<button className="btn btn-ghost" onClick={handleReadAll}>Mark all read</button>}
    >
      <div className="toolbar">
        <select
          value={unreadOnly ? "unread" : "all"}
          onChange={(e) => {
            const u = e.target.value === "unread";
            setUnreadOnly(u);
            load(u);
          }}
          style={{ padding: "12px 14px", borderRadius: 8, border: "1px solid var(--hms-border)" }}
        >
          <option value="all">All notifications</option>
          <option value="unread">Unread only</option>
        </select>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      {loading ? (
        <div className="alert-empty">Loading...</div>
      ) : notifications.length === 0 ? (
        <div className="alert-empty">
          Nothing here yet. Notifications appear when appointments complete and bills are paid.
        </div>
      ) : (
        notifications.map((n) => (
          <div
            key={n.id}
            className={`notif-item ${n.read ? "" : "unread"}`}
            onClick={() => handleRead(n)}
            style={{ cursor: n.read ? "default" : "pointer" }}
          >
            {!n.read && <div className="notif-dot" />}
            <div style={{ flex: 1 }}>
              <div className="title">{n.title}</div>
              <div className="msg">{n.message}</div>
              <div className="when">
                {n.type.replace("_", " ")} · {n.createdAt ? new Date(n.createdAt).toLocaleString() : ""}
              </div>
            </div>
          </div>
        ))
      )}
    </AppLayout>
  );
}

export default NotificationsPage;
