import api from "./axios";

export const listDoctors = ({ search, departmentId } = {}) => {
  const params = {};
  if (search) params.search = search;
  if (departmentId) params.departmentId = departmentId;
  return api.get("/doctors", { params }).then((r) => r.data);
};

export const getDoctor = (id) =>
  api.get(`/doctors/${id}`).then((r) => r.data);

export const createDoctor = (payload) =>
  api.post("/doctors", payload).then((r) => r.data);

export const updateDoctor = (id, payload) =>
  api.put(`/doctors/${id}`, payload).then((r) => r.data);

export const deleteDoctor = (id) =>
  api.delete(`/doctors/${id}`).then((r) => r.data);

// --- Availability slots ---
export const getSlots = (doctorId, date) =>
  api.get(`/doctors/${doctorId}/slots`, { params: date ? { date } : {} }).then((r) => r.data);

export const addSlot = (doctorId, payload) =>
  api.post(`/doctors/${doctorId}/slots`, payload).then((r) => r.data);

export const updateSlotStatus = (slotId, status) =>
  api.patch(`/slots/${slotId}`, { status }).then((r) => r.data);

export const deleteSlot = (slotId) =>
  api.delete(`/slots/${slotId}`).then((r) => r.data);
