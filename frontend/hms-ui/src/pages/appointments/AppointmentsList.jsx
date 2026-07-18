import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import AppLayout from "../../components/AppLayout";
import { listAppointments, cancelAppointment, completeAppointment } from "../../api/appointments";
import "../../styles/patients.css";
import "../../styles/appointments.css";

function AppointmentsList() {
  const navigate = useNavigate();
  const [appointments, setAppointments] = useState([]);
  const [status, setStatus] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  const load = async (s) => {
    setLoading(true);
    setError("");
    try {
      setAppointments(await listAppointments(s || undefined));
    } catch (e) {
      setError(e.response?.data?.message || "Failed to load appointments");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load("");
  }, []);

  const act = async (fn, id) => {
    try {
      await fn(id);
      load(status);
    } catch (e) {
      setError(e.response?.data?.message || "Action failed");
    }
  };

  return (
    <AppLayout
      title="Appointments"
      actions={
        <button className="btn btn-primary" onClick={() => navigate("/appointments/book")}>+ Book</button>
      }
    >
      <div className="toolbar">
        <select
          value={status}
          onChange={(e) => { setStatus(e.target.value); load(e.target.value); }}
          style={{ padding: "12px 14px", borderRadius: 8, border: "1px solid var(--hms-border)" }}
        >
          <option value="">All statuses</option>
          <option value="BOOKED">Booked</option>
          <option value="COMPLETED">Completed</option>
          <option value="CANCELLED">Cancelled</option>
        </select>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      {loading ? (
        <div className="alert-empty">Loading...</div>
      ) : appointments.length === 0 ? (
        <div className="alert-empty">No appointments yet.</div>
      ) : (
        <div className="table-wrap">
          <table className="patient-table">
            <thead>
              <tr>
                <th>Token</th>
                <th>Patient</th>
                <th>Doctor</th>
                <th>Department</th>
                <th>When</th>
                <th>Priority</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {appointments.map((a) => (
                <tr key={a.id}>
                  <td>#{a.tokenNumber}</td>
                  <td>{a.patientName}</td>
                  <td>{a.doctorName || "-"}</td>
                  <td>{a.department || "-"}</td>
                  <td>{a.date} {a.startTime && `· ${a.startTime}`}</td>
                  <td><span className={`urgency urgency-${a.urgency}`}>{a.urgencyLabel}</span></td>
                  <td><span className={`status-pill status-${a.status}`}>{a.status}</span></td>
                  <td>
                    <div className="row-actions">
                      {a.status === "BOOKED" && (
                        <>
                          <button className="btn btn-ghost btn-sm" onClick={() => act(completeAppointment, a.id)}>Complete</button>
                          <button className="btn btn-danger btn-sm" onClick={() => act(cancelAppointment, a.id)}>Cancel</button>
                        </>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </AppLayout>
  );
}

export default AppointmentsList;
