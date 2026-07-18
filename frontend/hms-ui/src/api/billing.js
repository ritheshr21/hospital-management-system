import api from "./axios";

export const listBills = (status) =>
  api.get("/bills", { params: status ? { status } : {} }).then((r) => r.data);

export const getBill = (id) =>
  api.get(`/bills/${id}`).then((r) => r.data);

export const getBillByAppointment = (appointmentId) =>
  api.get(`/bills/appointment/${appointmentId}`).then((r) => r.data);

export const createBill = (payload) =>
  api.post("/bills", payload).then((r) => r.data);

export const payBill = (id, payload) =>
  api.post(`/bills/${id}/pay`, payload).then((r) => r.data);

export const cancelBill = (id) =>
  api.post(`/bills/${id}/cancel`).then((r) => r.data);
