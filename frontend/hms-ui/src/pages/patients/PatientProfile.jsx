import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import AppLayout from "../../components/AppLayout";
import { getPatient } from "../../api/patients";
import "../../styles/patients.css";

function Field({ label, value }) {
  return (
    <div className="profile-item">
      <div className="label">{label}</div>
      <div className="value">{value || "-"}</div>
    </div>
  );
}

function PatientProfile() {
  const navigate = useNavigate();
  const { id } = useParams();
  const [patient, setPatient] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    getPatient(id)
      .then(setPatient)
      .catch((e) => setError(e.response?.data?.message || "Failed to load patient"));
  }, [id]);

  return (
    <AppLayout
      title="Patient Profile"
      actions={
        patient && (
          <button className="btn btn-primary" onClick={() => navigate(`/patients/${id}/edit`)}>
            Edit
          </button>
        )
      }
    >
      {error && <div className="alert alert-error">{error}</div>}

      {!patient ? (
        !error && <div className="alert-empty">Loading...</div>
      ) : (
        <div className="panel">
          <div className="page-header">
            <h2>{patient.name}</h2>
            <button className="btn btn-ghost" onClick={() => navigate("/patients")}>
              Back to list
            </button>
          </div>

          <div className="profile-grid">
            <Field label="Email" value={patient.email} />
            <Field label="Phone" value={patient.phone} />
            <Field label="Gender" value={patient.gender} />
            <Field label="Date of Birth" value={patient.dateOfBirth} />
            <Field label="Blood Group" value={patient.bloodGroup} />
            <Field label="Address" value={patient.address} />
          </div>

          <div className="profile-section">
            <h3 className="form-section-title">Emergency Contact</h3>
            <div className="profile-grid">
              <Field label="Name" value={patient.emergencyContact?.contactName} />
              <Field label="Relationship" value={patient.emergencyContact?.relationship} />
              <Field label="Phone" value={patient.emergencyContact?.contactPhone} />
            </div>
          </div>

          <div className="profile-section">
            <h3 className="form-section-title">Medical History</h3>
            {patient.medicalHistory?.length ? (
              patient.medicalHistory.map((h) => (
                <div className="history-card" key={h.id}>
                  <div className="cond">{h.condition}</div>
                  {h.notes && <div className="notes">{h.notes}</div>}
                </div>
              ))
            ) : (
              <p className="app-subtitle">No medical history recorded.</p>
            )}
          </div>
        </div>
      )}
    </AppLayout>
  );
}

export default PatientProfile;
