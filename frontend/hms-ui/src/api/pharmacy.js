import api from "./axios";

// --- Medicines / inventory ---
export const listMedicines = ({ search, lowStock } = {}) => {
  const params = {};
  if (search) params.search = search;
  if (lowStock) params.lowStock = true;
  return api.get("/medicines", { params }).then((r) => r.data);
};

export const getMedicine = (id) =>
  api.get(`/medicines/${id}`).then((r) => r.data);

export const createMedicine = (payload) =>
  api.post("/medicines", payload).then((r) => r.data);

export const updateMedicine = (id, payload) =>
  api.put(`/medicines/${id}`, payload).then((r) => r.data);

export const restockMedicine = (id, quantity) =>
  api.post(`/medicines/${id}/restock`, { quantity }).then((r) => r.data);

export const deleteMedicine = (id) =>
  api.delete(`/medicines/${id}`).then((r) => r.data);

// --- Prescriptions ---
export const listPrescriptions = (status) =>
  api.get("/prescriptions", { params: status ? { status } : {} }).then((r) => r.data);

export const getPrescription = (id) =>
  api.get(`/prescriptions/${id}`).then((r) => r.data);

export const createPrescription = (payload) =>
  api.post("/prescriptions", payload).then((r) => r.data);

export const dispensePrescription = (id) =>
  api.post(`/prescriptions/${id}/dispense`).then((r) => r.data);

export const cancelPrescription = (id) =>
  api.post(`/prescriptions/${id}/cancel`).then((r) => r.data);
