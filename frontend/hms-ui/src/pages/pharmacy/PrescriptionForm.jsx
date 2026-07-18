import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import AppLayout from "../../components/AppLayout";
import { createPrescription, listMedicines } from "../../api/pharmacy";
import "../../styles/patients.css";
import "../../styles/pharmacy.css";

const emptyItem = { medicineId: "", dosage: "1-0-1", durationDays: 5, quantity: 10, instructions: "" };

function PrescriptionForm() {
  const navigate = useNavigate();
  const [medicines, setMedicines] = useState([]);
  const [form, setForm] = useState({
    patientName: "",
    doctorName: localStorage.getItem("name") || "",
    notes: "",
    items: [{ ...emptyItem }]
  });
  const [error, setError] = useState("");
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    listMedicines().then((m) => setMedicines(m.filter((x) => x.active))).catch(() => {});
  }, []);

  const setItem = (i, name, value) =>
    setForm((f) => {
      const items = [...f.items];
      items[i] = { ...items[i], [name]: value };
      return { ...f, items };
    });

  const addItem = () => setForm((f) => ({ ...f, items: [...f.items, { ...emptyItem }] }));
  const removeItem = (i) =>
    setForm((f) => ({ ...f, items: f.items.filter((_, idx) => idx !== i) }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSaving(true);
    try {
      const payload = {
        ...form,
        items: form.items
          .filter((it) => it.medicineId)
          .map((it) => ({
            ...it,
            durationDays: Number(it.durationDays) || 0,
            quantity: Number(it.quantity) || 1
          }))
      };
      if (payload.items.length === 0) {
        setError("Add at least one medicine");
        setSaving(false);
        return;
      }
      await createPrescription(payload);
      navigate("/prescriptions");
    } catch (e) {
      setError(e.response?.data?.message || "Failed to create prescription");
      setSaving(false);
    }
  };

  const stockFor = (id) => medicines.find((m) => m.id === id);

  return (
    <AppLayout title="New Prescription">
      <div className="panel">
        {error && <div className="alert alert-error">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-grid">
            <div className="form-field">
              <label>Patient Name *</label>
              <input value={form.patientName} onChange={(e) => setForm({ ...form, patientName: e.target.value })} required />
            </div>
            <div className="form-field">
              <label>Prescribing Doctor</label>
              <input value={form.doctorName} onChange={(e) => setForm({ ...form, doctorName: e.target.value })} />
            </div>
            <div className="form-field full">
              <label>Notes</label>
              <input value={form.notes} onChange={(e) => setForm({ ...form, notes: e.target.value })} placeholder="e.g. take after food" />
            </div>
          </div>

          <h3 className="form-section-title">Medicines</h3>
          {form.items.map((it, i) => {
            const med = stockFor(it.medicineId);
            return (
              <div key={i}>
                <div className="rx-row">
                  <select value={it.medicineId} onChange={(e) => setItem(i, "medicineId", e.target.value)}>
                    <option value="">Select medicine...</option>
                    {medicines.map((m) => (
                      <option key={m.id} value={m.id}>
                        {m.name} ({m.stockQuantity} in stock)
                      </option>
                    ))}
                  </select>
                  <input placeholder="Dosage" value={it.dosage} onChange={(e) => setItem(i, "dosage", e.target.value)} />
                  <input type="number" min="0" placeholder="Days" value={it.durationDays} onChange={(e) => setItem(i, "durationDays", e.target.value)} />
                  <input type="number" min="1" placeholder="Qty" value={it.quantity} onChange={(e) => setItem(i, "quantity", e.target.value)} />
                  {form.items.length > 1 && (
                    <button type="button" className="btn btn-danger btn-sm" onClick={() => removeItem(i)}>Remove</button>
                  )}
                </div>
                {med && Number(it.quantity) > med.stockQuantity && (
                  <div style={{ color: "#b91c1c", fontSize: 13, marginTop: -4, marginBottom: 10 }}>
                    Only {med.stockQuantity} of {med.name} in stock — dispensing will be rejected.
                  </div>
                )}
              </div>
            );
          })}
          <button type="button" className="btn btn-ghost btn-sm" onClick={addItem}>+ Add medicine</button>

          <div className="form-actions">
            <button className="btn btn-primary" type="submit" disabled={saving}>
              {saving ? "Saving..." : "Create Prescription"}
            </button>
            <button type="button" className="btn btn-ghost" onClick={() => navigate("/prescriptions")}>Cancel</button>
          </div>
        </form>
      </div>
    </AppLayout>
  );
}

export default PrescriptionForm;
