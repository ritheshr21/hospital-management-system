import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import AppLayout from "../../components/AppLayout";
import { createPatient, getPatient, updatePatient } from "../../api/patients";
import "../../styles/patients.css";

const emptyForm = {
  name: "",
  email: "",
  phone: "",
  gender: "",
  dateOfBirth: "",
  bloodGroup: "",
  address: "",
  emergencyContact: { contactName: "", relationship: "", contactPhone: "" },
  medicalHistory: []
};

function PatientForm() {
  const navigate = useNavigate();
  const { id } = useParams();
  const isEdit = Boolean(id);

  const [form, setForm] = useState(emptyForm);
  const [error, setError] = useState("");
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (!isEdit) return;
    getPatient(id)
      .then((p) =>
        setForm({
          ...emptyForm,
          ...p,
          dateOfBirth: p.dateOfBirth || "",
          gender: p.gender || "",
          emergencyContact: p.emergencyContact || emptyForm.emergencyContact,
          medicalHistory: p.medicalHistory || []
        })
      )
      .catch((e) => setError(e.response?.data?.message || "Failed to load patient"));
  }, [id, isEdit]);

  const setField = (name, value) => setForm((f) => ({ ...f, [name]: value }));
  const setContact = (name, value) =>
    setForm((f) => ({ ...f, emergencyContact: { ...f.emergencyContact, [name]: value } }));

  const addHistory = () =>
    setForm((f) => ({ ...f, medicalHistory: [...f.medicalHistory, { condition: "", notes: "" }] }));
  const setHistory = (i, name, value) =>
    setForm((f) => {
      const next = [...f.medicalHistory];
      next[i] = { ...next[i], [name]: value };
      return { ...f, medicalHistory: next };
    });
  const removeHistory = (i) =>
    setForm((f) => ({ ...f, medicalHistory: f.medicalHistory.filter((_, idx) => idx !== i) }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSaving(true);

    // Drop empty optional blocks so we don't send noise to the backend.
    const payload = {
      ...form,
      gender: form.gender || null,
      dateOfBirth: form.dateOfBirth || null,
      medicalHistory: form.medicalHistory.filter((h) => h.condition?.trim())
    };

    try {
      if (isEdit) {
        await updatePatient(id, payload);
        navigate(`/patients/${id}`);
      } else {
        const created = await createPatient(payload);
        navigate(`/patients/${created.id}`);
      }
    } catch (e) {
      setError(e.response?.data?.message || "Failed to save patient");
      setSaving(false);
    }
  };

  return (
    <AppLayout title={isEdit ? "Edit Patient" : "Add Patient"}>
      <div className="panel">
        {error && <div className="alert alert-error">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-grid">
            <div className="form-field">
              <label>Full Name *</label>
              <input value={form.name} onChange={(e) => setField("name", e.target.value)} required />
            </div>
            <div className="form-field">
              <label>Email *</label>
              <input type="email" value={form.email} onChange={(e) => setField("email", e.target.value)} required />
            </div>
            <div className="form-field">
              <label>Phone</label>
              <input value={form.phone} onChange={(e) => setField("phone", e.target.value)} />
            </div>
            <div className="form-field">
              <label>Gender</label>
              <select value={form.gender} onChange={(e) => setField("gender", e.target.value)}>
                <option value="">Select...</option>
                <option value="MALE">Male</option>
                <option value="FEMALE">Female</option>
                <option value="OTHER">Other</option>
              </select>
            </div>
            <div className="form-field">
              <label>Date of Birth</label>
              <input type="date" value={form.dateOfBirth} onChange={(e) => setField("dateOfBirth", e.target.value)} />
            </div>
            <div className="form-field">
              <label>Blood Group</label>
              <input value={form.bloodGroup} onChange={(e) => setField("bloodGroup", e.target.value)} placeholder="e.g. O+" />
            </div>
            <div className="form-field full">
              <label>Address</label>
              <input value={form.address} onChange={(e) => setField("address", e.target.value)} />
            </div>

            <h3 className="form-section-title">Emergency Contact</h3>
            <div className="form-field">
              <label>Contact Name</label>
              <input value={form.emergencyContact.contactName} onChange={(e) => setContact("contactName", e.target.value)} />
            </div>
            <div className="form-field">
              <label>Relationship</label>
              <input value={form.emergencyContact.relationship} onChange={(e) => setContact("relationship", e.target.value)} />
            </div>
            <div className="form-field">
              <label>Contact Phone</label>
              <input value={form.emergencyContact.contactPhone} onChange={(e) => setContact("contactPhone", e.target.value)} />
            </div>

            <h3 className="form-section-title">Medical History</h3>
            <div className="form-field full">
              {form.medicalHistory.map((h, i) => (
                <div className="history-row" key={i}>
                  <input
                    placeholder="Condition"
                    value={h.condition || ""}
                    onChange={(e) => setHistory(i, "condition", e.target.value)}
                  />
                  <input
                    placeholder="Notes"
                    value={h.notes || ""}
                    onChange={(e) => setHistory(i, "notes", e.target.value)}
                  />
                  <button type="button" className="btn btn-danger btn-sm" onClick={() => removeHistory(i)}>
                    Remove
                  </button>
                </div>
              ))}
              <button type="button" className="btn btn-ghost btn-sm" onClick={addHistory}>
                + Add condition
              </button>
            </div>
          </div>

          <div className="form-actions">
            <button className="btn btn-primary" type="submit" disabled={saving}>
              {saving ? "Saving..." : isEdit ? "Update Patient" : "Create Patient"}
            </button>
            <button type="button" className="btn btn-ghost" onClick={() => navigate(-1)}>
              Cancel
            </button>
          </div>
        </form>
      </div>
    </AppLayout>
  );
}

export default PatientForm;
