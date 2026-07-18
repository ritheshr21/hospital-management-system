import api from "./axios";

// All patient calls go through the gateway (/patients), which validates the JWT.
export const listPatients = (search) =>
  api.get("/patients", { params: search ? { search } : {} }).then((r) => r.data);

export const getPatient = (id) =>
  api.get(`/patients/${id}`).then((r) => r.data);

export const createPatient = (payload) =>
  api.post("/patients", payload).then((r) => r.data);

export const updatePatient = (id, payload) =>
  api.put(`/patients/${id}`, payload).then((r) => r.data);

export const deletePatient = (id) =>
  api.delete(`/patients/${id}`).then((r) => r.data);
