import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import AppLayout from "../../components/AppLayout";
import { cancelPrescription, dispensePrescription, listPrescriptions } from "../../api/pharmacy";
import "../../styles/patients.css";
import "../../styles/pharmacy.css";

function PrescriptionsList() {
  const navigate = useNavigate();
  const [prescriptions, setPrescriptions] = useState([]);
  const [status, setStatus] = useState("");
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");
  const [loading, setLoading] = useState(true);

  const load = async (s) => {
    setLoading(true);
    setError("");
    try {
      setPrescriptions(await listPrescriptions(s || undefined));
    } catch (e) {
      setError(e.response?.data?.message || "Failed to load prescriptions");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load("");
  }, []);

  const handleDispense = async (rx) => {
    setError("");
    setNotice("");
    try {
      await dispensePrescription(rx.id);
      setNotice(`Dispensed for ${rx.patientName} — stock updated.`);
      load(status);
    } catch (e) {
      // Insufficient stock comes back as a 409 with a specific message.
      setError(e.response?.data?.message || "Dispense failed");
    }
  };

  const handleCancel = async (rx) => {
    if (!window.confirm(`Cancel prescription for ${rx.patientName}?`)) return;
    try {
      await cancelPrescription(rx.id);
      load(status);
    } catch (e) {
      setError(e.response?.data?.message || "Cancel failed");
    }
  };

  return (
    <AppLayout
      title="Prescriptions"
      actions={
        <>
          <button className="btn btn-ghost" onClick={() => navigate("/pharmacy")}>Pharmacy</button>
          <button className="btn btn-primary" onClick={() => navigate("/prescriptions/new")}>+ New</button>
        </>
      }
    >
      <div className="toolbar">
        <select
          value={status}
          onChange={(e) => { setStatus(e.target.value); load(e.target.value); }}
          style={{ padding: "12px 14px", borderRadius: 8, border: "1px solid var(--hms-border)" }}
        >
          <option value="">All prescriptions</option>
          <option value="ISSUED">Issued</option>
          <option value="DISPENSED">Dispensed</option>
          <option value="CANCELLED">Cancelled</option>
        </select>
      </div>

      {error && <div className="alert alert-error">{error}</div>}
      {notice && <div className="alert" style={{ background: "#f0fdf4", color: "#166534" }}>{notice}</div>}

      {loading ? (
        <div className="alert-empty">Loading...</div>
      ) : prescriptions.length === 0 ? (
        <div className="alert-empty">No prescriptions yet.</div>
      ) : (
        prescriptions.map((rx) => (
          <div className="panel" key={rx.id} style={{ marginBottom: 16 }}>
            <div className="page-header" style={{ marginBottom: 12 }}>
              <div>
                <h3 style={{ margin: 0 }}>{rx.patientName}</h3>
                <div className="app-subtitle">
                  {rx.doctorName ? `by ${rx.doctorName} · ` : ""}
                  {rx.createdAt ? new Date(rx.createdAt).toLocaleString() : ""}
                </div>
              </div>
              <div style={{ display: "flex", gap: 8, alignItems: "center" }}>
                <span className={`rx-status rx-${rx.status}`}>{rx.status}</span>
                {rx.status === "ISSUED" && (
                  <>
                    <button className="btn btn-primary btn-sm" onClick={() => handleDispense(rx)}>Dispense</button>
                    <button className="btn btn-danger btn-sm" onClick={() => handleCancel(rx)}>Cancel</button>
                  </>
                )}
              </div>
            </div>

            {rx.items?.map((it) => (
              <div className="rx-card" key={it.id}>
                <div className="med">{it.medicineName} × {it.quantity}</div>
                <div className="detail">
                  {it.dosage ? `Dosage ${it.dosage}` : ""}
                  {it.durationDays ? ` · ${it.durationDays} days` : ""}
                  {it.instructions ? ` · ${it.instructions}` : ""}
                </div>
              </div>
            ))}
            {rx.notes && <div className="app-subtitle" style={{ marginTop: 8 }}>Notes: {rx.notes}</div>}
          </div>
        ))
      )}
    </AppLayout>
  );
}

export default PrescriptionsList;
