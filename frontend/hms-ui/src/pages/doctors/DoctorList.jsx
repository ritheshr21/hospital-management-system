import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import AppLayout from "../../components/AppLayout";
import { listDoctors } from "../../api/doctors";
import { listDepartments } from "../../api/departments";
import "../../styles/patients.css";
import "../../styles/doctors.css";

function DoctorList() {
  const navigate = useNavigate();
  const [doctors, setDoctors] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [search, setSearch] = useState("");
  const [departmentId, setDepartmentId] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  const load = async (params) => {
    setLoading(true);
    setError("");
    try {
      setDoctors(await listDoctors(params));
    } catch (e) {
      setError(e.response?.data?.message || "Failed to load doctors");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    listDepartments().then(setDepartments).catch(() => {});
    load({});
  }, []);

  const applyFilters = (e) => {
    e?.preventDefault();
    load({ search, departmentId });
  };

  return (
    <AppLayout
      title="Doctors"
      actions={
        <button className="btn btn-primary" onClick={() => navigate("/doctors/new")}>
          + Add Doctor
        </button>
      }
    >
      <form className="toolbar" onSubmit={applyFilters}>
        <input
          placeholder="Search by name, email or specialization..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
        <select
          value={departmentId}
          onChange={(e) => {
            const v = e.target.value;
            setDepartmentId(v);
            load({ search, departmentId: v });
          }}
          style={{ padding: "12px 14px", borderRadius: 8, border: "1px solid var(--hms-border)" }}
        >
          <option value="">All departments</option>
          {departments.map((d) => (
            <option key={d.id} value={d.id}>{d.name}</option>
          ))}
        </select>
        <button className="btn btn-primary" type="submit">Search</button>
      </form>

      {error && <div className="alert alert-error">{error}</div>}

      {loading ? (
        <div className="alert-empty">Loading...</div>
      ) : doctors.length === 0 ? (
        <div className="alert-empty">No doctors found. Add the first one.</div>
      ) : (
        <div className="doctor-cards">
          {doctors.map((d) => (
            <div key={d.id} className="doctor-card" onClick={() => navigate(`/doctors/${d.id}`)}>
              <h3>{d.name}</h3>
              <div className="spec">{d.specialization || "General"}</div>
              <div className="meta">
                {d.department?.name || "Unassigned"}
                {d.consultationFee != null && <> · ₹{d.consultationFee}</>}
              </div>
              <div className="meta">
                <span className={`badge ${d.active ? "badge-active" : "badge-inactive"}`}>
                  {d.active ? "Active" : "Inactive"}
                </span>
              </div>
            </div>
          ))}
        </div>
      )}
    </AppLayout>
  );
}

export default DoctorList;
