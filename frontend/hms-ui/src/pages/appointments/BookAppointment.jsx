import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import AppLayout from "../../components/AppLayout";
import { listDoctors, getSlots } from "../../api/doctors";
import { bookAppointment, previewTriage } from "../../api/appointments";
import "../../styles/patients.css";
import "../../styles/appointments.css";

const empty = {
  patientName: "",
  doctorId: "",
  slotId: "",
  symptoms: "",
  age: "",
  sex: ""
};

function UrgencyBadge({ urgency, label }) {
  return <span className={`urgency urgency-${urgency}`}>{label || `LEVEL ${urgency}`}</span>;
}

function BookAppointment() {
  const navigate = useNavigate();
  const [form, setForm] = useState(empty);
  const [doctors, setDoctors] = useState([]);
  const [slots, setSlots] = useState([]);
  const [triage, setTriage] = useState(null);
  const [triaging, setTriaging] = useState(false);
  const [booked, setBooked] = useState(null);
  const [error, setError] = useState("");
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    listDoctors().then((d) => setDoctors(d.filter((x) => x.active))).catch(() => {});
  }, []);

  const chosenDoctor = doctors.find((d) => d.id === form.doctorId);
  const chosenSlot = slots.find((s) => s.id === form.slotId);

  const onDoctorChange = async (doctorId) => {
    setForm((f) => ({ ...f, doctorId, slotId: "" }));
    setSlots([]);
    if (doctorId) {
      try {
        const s = await getSlots(doctorId);
        setSlots(s.filter((x) => x.status === "AVAILABLE"));
      } catch {
        setSlots([]);
      }
    }
  };

  const handlePreview = async () => {
    if (!form.symptoms.trim()) {
      setError("Enter symptoms first to run triage");
      return;
    }
    setError("");
    setTriaging(true);
    try {
      const t = await previewTriage({
        symptoms: form.symptoms,
        age: form.age ? Number(form.age) : null,
        sex: form.sex || null
      });
      setTriage(t);
    } catch (e) {
      setError(e.response?.data?.message || "Triage unavailable (is GROQ_API_KEY set?)");
    } finally {
      setTriaging(false);
    }
  };

  const handleBook = async (e) => {
    e.preventDefault();
    setError("");
    setSaving(true);
    try {
      const payload = {
        patientName: form.patientName,
        doctorId: form.doctorId,
        slotId: form.slotId || null,
        date: chosenSlot?.date,
        startTime: chosenSlot?.startTime,
        endTime: chosenSlot?.endTime,
        symptoms: form.symptoms,
        age: form.age ? Number(form.age) : null,
        sex: form.sex || null
      };
      const result = await bookAppointment(payload);
      setBooked(result);
    } catch (e) {
      setError(e.response?.data?.message || "Failed to book appointment");
    } finally {
      setSaving(false);
    }
  };

  if (booked) {
    return (
      <AppLayout title="Appointment Booked">
        <div className="panel">
          <div className="token-box">
            <div className="lbl">Your Token Number</div>
            <div className="num">#{booked.tokenNumber}</div>
          </div>
          <div className="profile-grid">
            <div className="profile-item"><div className="label">Patient</div><div className="value">{booked.patientName}</div></div>
            <div className="profile-item"><div className="label">Doctor</div><div className="value">{booked.doctorName}</div></div>
            <div className="profile-item"><div className="label">Department</div><div className="value">{booked.department}</div></div>
            <div className="profile-item"><div className="label">When</div><div className="value">{booked.date} {booked.startTime}–{booked.endTime}</div></div>
            <div className="profile-item">
              <div className="label">Triage Priority</div>
              <div className="value"><UrgencyBadge urgency={booked.urgency} label={booked.urgencyLabel} /></div>
            </div>
          </div>
          {booked.recommendedAction && (
            <div className="triage-card" style={{ marginTop: 18 }}>
              <div className="action"><strong>Recommended:</strong> {booked.recommendedAction}</div>
              {booked.triageReason && <div className="reason">{booked.triageReason}</div>}
            </div>
          )}
          <div className="form-actions">
            <button className="btn btn-primary" onClick={() => navigate("/queue")}>View Live Queue</button>
            <button className="btn btn-ghost" onClick={() => { setBooked(null); setForm(empty); setTriage(null); }}>
              Book Another
            </button>
          </div>
        </div>
      </AppLayout>
    );
  }

  return (
    <AppLayout title="Book Appointment">
      <div className="panel">
        {error && <div className="alert alert-error">{error}</div>}

        <form onSubmit={handleBook}>
          <div className="form-grid">
            <div className="form-field">
              <label>Patient Name *</label>
              <input value={form.patientName} onChange={(e) => setForm({ ...form, patientName: e.target.value })} required />
            </div>
            <div className="form-field">
              <label>Doctor *</label>
              <select value={form.doctorId} onChange={(e) => onDoctorChange(e.target.value)} required>
                <option value="">Select doctor...</option>
                {doctors.map((d) => (
                  <option key={d.id} value={d.id}>
                    {d.name} — {d.specialization || "General"} {d.department ? `(${d.department.name})` : ""}
                  </option>
                ))}
              </select>
            </div>
            <div className="form-field">
              <label>Available Slot</label>
              <select value={form.slotId} onChange={(e) => setForm({ ...form, slotId: e.target.value })} disabled={!form.doctorId}>
                <option value="">{form.doctorId ? (slots.length ? "Select slot..." : "No available slots") : "Pick a doctor first"}</option>
                {slots.map((s) => (
                  <option key={s.id} value={s.id}>{s.date} · {s.startTime}–{s.endTime}</option>
                ))}
              </select>
            </div>
            <div className="form-field">
              <label>Age</label>
              <input type="number" min="0" value={form.age} onChange={(e) => setForm({ ...form, age: e.target.value })} />
            </div>
            <div className="form-field">
              <label>Sex</label>
              <select value={form.sex} onChange={(e) => setForm({ ...form, sex: e.target.value })}>
                <option value="">-</option>
                <option value="Male">Male</option>
                <option value="Female">Female</option>
                <option value="Other">Other</option>
              </select>
            </div>
            <div className="form-field full">
              <label>Symptoms * (used by AI triage to set priority)</label>
              <textarea
                rows={3}
                value={form.symptoms}
                onChange={(e) => setForm({ ...form, symptoms: e.target.value })}
                placeholder="e.g. severe chest pain and shortness of breath for the last hour"
                required
              />
            </div>
          </div>

          <div className="form-actions" style={{ marginTop: 16 }}>
            <button type="button" className="btn btn-ghost" onClick={handlePreview} disabled={triaging}>
              {triaging ? "Analyzing..." : "🩺 Preview AI Triage"}
            </button>
          </div>

          {triage && (
            <div className="triage-card">
              <div className="row">
                <span className="dept">{triage.department}</span>
                <UrgencyBadge urgency={triage.urgency} label={triage.urgencyLabel} />
              </div>
              <div className="action"><strong>Recommended:</strong> {triage.recommendedAction}</div>
              {triage.reason && <div className="reason">{triage.reason}</div>}
            </div>
          )}

          <div className="form-actions">
            <button className="btn btn-primary" type="submit" disabled={saving}>
              {saving ? "Booking..." : "Book Appointment"}
            </button>
            <button type="button" className="btn btn-ghost" onClick={() => navigate("/dashboard")}>Cancel</button>
          </div>
        </form>
      </div>
    </AppLayout>
  );
}

export default BookAppointment;
