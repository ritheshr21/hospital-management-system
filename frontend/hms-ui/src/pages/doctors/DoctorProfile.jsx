import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import AppLayout from "../../components/AppLayout";
import {
  addSlot,
  deleteDoctor,
  deleteSlot,
  getDoctor,
  getSlots,
  updateSlotStatus
} from "../../api/doctors";
import "../../styles/patients.css";
import "../../styles/doctors.css";

function Field({ label, value }) {
  return (
    <div className="profile-item">
      <div className="label">{label}</div>
      <div className="value">{value ?? "-"}</div>
    </div>
  );
}

const emptySlot = { date: "", startTime: "", endTime: "" };

function DoctorProfile() {
  const navigate = useNavigate();
  const { id } = useParams();
  const [doctor, setDoctor] = useState(null);
  const [slots, setSlots] = useState([]);
  const [slotForm, setSlotForm] = useState(emptySlot);
  const [error, setError] = useState("");

  const loadSlots = () => getSlots(id).then(setSlots).catch(() => {});

  useEffect(() => {
    getDoctor(id)
      .then(setDoctor)
      .catch((e) => setError(e.response?.data?.message || "Failed to load doctor"));
    loadSlots();
  }, [id]);

  const handleAddSlot = async (e) => {
    e.preventDefault();
    setError("");
    try {
      await addSlot(id, slotForm);
      setSlotForm(emptySlot);
      loadSlots();
    } catch (e) {
      setError(e.response?.data?.message || "Failed to add slot");
    }
  };

  const toggleBlock = async (slot) => {
    const next = slot.status === "BLOCKED" ? "AVAILABLE" : "BLOCKED";
    try {
      await updateSlotStatus(slot.id, next);
      loadSlots();
    } catch (e) {
      setError(e.response?.data?.message || "Failed to update slot");
    }
  };

  const removeSlot = async (slot) => {
    try {
      await deleteSlot(slot.id);
      loadSlots();
    } catch (e) {
      setError(e.response?.data?.message || "Failed to delete slot");
    }
  };

  const handleDeleteDoctor = async () => {
    if (!window.confirm(`Delete doctor "${doctor.name}"? This removes their slots too.`)) return;
    try {
      await deleteDoctor(id);
      navigate("/doctors");
    } catch (e) {
      setError(e.response?.data?.message || "Failed to delete doctor");
    }
  };

  return (
    <AppLayout
      title="Doctor Profile"
      actions={
        doctor && (
          <>
            <button className="btn btn-primary" onClick={() => navigate(`/doctors/${id}/edit`)}>Edit</button>
            <button className="btn btn-danger" onClick={handleDeleteDoctor}>Delete</button>
          </>
        )
      }
    >
      {error && <div className="alert alert-error">{error}</div>}

      {!doctor ? (
        !error && <div className="alert-empty">Loading...</div>
      ) : (
        <>
          <div className="panel">
            <div className="page-header">
              <div>
                <h2 style={{ marginBottom: 4 }}>{doctor.name}</h2>
                <span className="spec" style={{ color: "var(--hms-primary)" }}>
                  {doctor.specialization || "General"}
                </span>{" "}
                <span className={`badge ${doctor.active ? "badge-active" : "badge-inactive"}`}>
                  {doctor.active ? "Active" : "Inactive"}
                </span>
              </div>
              <button className="btn btn-ghost" onClick={() => navigate("/doctors")}>Back to list</button>
            </div>

            <div className="profile-grid">
              <Field label="Email" value={doctor.email} />
              <Field label="Phone" value={doctor.phone} />
              <Field label="Qualification" value={doctor.qualification} />
              <Field label="Department" value={doctor.department?.name} />
              <Field label="Consultation Fee" value={doctor.consultationFee != null ? `₹${doctor.consultationFee}` : "-"} />
            </div>
          </div>

          <div className="panel" style={{ marginTop: 24 }}>
            <h3 className="form-section-title" style={{ marginTop: 0 }}>Availability</h3>

            <form onSubmit={handleAddSlot} className="history-row" style={{ gridTemplateColumns: "1fr 1fr 1fr auto", marginBottom: 20 }}>
              <input type="date" required value={slotForm.date} onChange={(e) => setSlotForm({ ...slotForm, date: e.target.value })} />
              <input type="time" required value={slotForm.startTime} onChange={(e) => setSlotForm({ ...slotForm, startTime: e.target.value })} />
              <input type="time" required value={slotForm.endTime} onChange={(e) => setSlotForm({ ...slotForm, endTime: e.target.value })} />
              <button className="btn btn-primary" type="submit">Add Slot</button>
            </form>

            {slots.length === 0 ? (
              <p className="app-subtitle">No availability slots yet.</p>
            ) : (
              slots.map((s) => (
                <div className="slot-row" key={s.id}>
                  <div className="slot-info">
                    <span className="slot-date">{s.date}</span>
                    <span className="slot-time">{s.startTime} – {s.endTime}</span>
                    <span className={`badge badge-${s.status}`}>{s.status}</span>
                  </div>
                  <div className="row-actions">
                    {s.status !== "BOOKED" && (
                      <button className="btn btn-ghost btn-sm" onClick={() => toggleBlock(s)}>
                        {s.status === "BLOCKED" ? "Unblock" : "Block"}
                      </button>
                    )}
                    <button className="btn btn-danger btn-sm" onClick={() => removeSlot(s)}>Delete</button>
                  </div>
                </div>
              ))
            )}
          </div>
        </>
      )}
    </AppLayout>
  );
}

export default DoctorProfile;
