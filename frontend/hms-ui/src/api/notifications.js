import api from "./axios";

export const listNotifications = (unread = false) =>
  api.get("/notifications", { params: unread ? { unread: true } : {} }).then((r) => r.data);

export const unreadCount = () =>
  api.get("/notifications/unread-count").then((r) => r.data.count);

export const markRead = (id) =>
  api.post(`/notifications/${id}/read`).then((r) => r.data);

export const markAllRead = () =>
  api.post("/notifications/read-all").then((r) => r.data);
