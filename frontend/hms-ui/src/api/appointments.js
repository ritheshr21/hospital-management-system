import api from "./axios";

export const bookAppointment = (payload) =>
  api.post("/appointments", payload).then((r) => r.data);

export const listAppointments = (status) =>
  api.get("/appointments", { params: status ? { status } : {} }).then((r) => r.data);

export const getAppointment = (id) =>
  api.get(`/appointments/${id}`).then((r) => r.data);

export const getQueue = () =>
  api.get("/appointments/queue").then((r) => r.data);

export const cancelAppointment = (id) =>
  api.post(`/appointments/${id}/cancel`).then((r) => r.data);

export const completeAppointment = (id) =>
  api.post(`/appointments/${id}/complete`).then((r) => r.data);

// Optional pre-booking triage preview (goes to triage-service directly)
export const previewTriage = (payload) =>
  api.post("/triage", payload).then((r) => r.data);
