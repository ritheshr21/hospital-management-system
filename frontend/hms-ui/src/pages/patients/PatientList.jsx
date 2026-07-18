import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import AppLayout from "../../components/AppLayout";
import { deletePatient, listPatients } from "../../api/patients";
import "../../styles/patients.css";

function PatientList() {
  const navigate = useNavigate();
  const [patients, setPatients] = useState([]);
  const [search, setSearch] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  const load = async (q) => {
    setLoading(true);
    setError("");
    try {
      setPatients(await listPatients(q));
    } catch (e) {
      setError(e.response?.data?.message || "Failed to load patients");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load("");
  }, []);

  const handleSearch = (e) => {
    e.preventDefault();
    load(search);
  };

  const handleDelete = async (id, name) => {
    if (!window.confirm(`Delete patient "${name}"? This cannot be undone.`)) return;
    try {
      await deletePatient(id);
      setPatients((prev) => prev.filter((p) => p.id !== id));
    } catch (e) {
      setError(e.response?.data?.message || "Failed to delete patient");
    }
  };

  return (
    <AppLayout
      title="Patients"
      actions={
        <button className="btn btn-primary" onClick={() => navigate("/patients/new")}>
          + Add Patient
        </button>
      }
    >
      <form className="toolbar" onSubmit={handleSearch}>
        <input
          placeholder="Search by name, email or phone..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
        <button className="btn btn-primary" type="submit">Search</button>
        {search && (
          <button
            type="button"
            className="btn btn-ghost"
            onClick={() => {
              setSearch("");
              load("");
            }}
          >
            Clear
          </button>
        )}
      </form>

      {error && <div className="alert alert-error">{error}</div>}

      {loading ? (
        <div className="alert-empty">Loading...</div>
      ) : patients.length === 0 ? (
        <div className="alert-empty">No patients found. Add the first one.</div>
      ) : (
        <div className="table-wrap">
          <table className="patient-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Gender</th>
                <th>Blood</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {patients.map((p) => (
                <tr key={p.id}>
                  <td>{p.name}</td>
                  <td>{p.email}</td>
                  <td>{p.phone || "-"}</td>
                  <td>{p.gender || "-"}</td>
                  <td>{p.bloodGroup || "-"}</td>
                  <td>
                    <div className="row-actions">
                      <button className="btn btn-ghost btn-sm" onClick={() => navigate(`/patients/${p.id}`)}>
                        View
                      </button>
                      <button className="btn btn-ghost btn-sm" onClick={() => navigate(`/patients/${p.id}/edit`)}>
                        Edit
                      </button>
                      <button className="btn btn-danger btn-sm" onClick={() => handleDelete(p.id, p.name)}>
                        Delete
                      </button>
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

export default PatientList;
