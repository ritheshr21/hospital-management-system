import { useEffect, useState } from "react";
import AppLayout from "../../components/AppLayout";
import {
  createDepartment,
  deleteDepartment,
  listDepartments,
  updateDepartment
} from "../../api/departments";
import "../../styles/patients.css";

const emptyForm = { name: "", description: "" };

function DepartmentsPage() {
  const [departments, setDepartments] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  const load = async () => {
    setLoading(true);
    try {
      setDepartments(await listDepartments());
    } catch (e) {
      setError(e.response?.data?.message || "Failed to load departments");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const resetForm = () => {
    setForm(emptyForm);
    setEditingId(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      if (editingId) {
        await updateDepartment(editingId, form);
      } else {
        await createDepartment(form);
      }
      resetForm();
      load();
    } catch (e) {
      setError(e.response?.data?.message || "Failed to save department");
    }
  };

  const startEdit = (d) => {
    setEditingId(d.id);
    setForm({ name: d.name, description: d.description || "" });
  };

  const handleDelete = async (d) => {
    if (!window.confirm(`Delete department "${d.name}"?`)) return;
    try {
      await deleteDepartment(d.id);
      load();
    } catch (e) {
      setError(e.response?.data?.message || "Failed to delete department");
    }
  };

  return (
    <AppLayout title="Departments">
      {error && <div className="alert alert-error">{error}</div>}

      <div className="panel" style={{ marginBottom: 24 }}>
        <h3 className="form-section-title" style={{ marginTop: 0 }}>
          {editingId ? "Edit Department" : "Add Department"}
        </h3>
        <form onSubmit={handleSubmit}>
          <div className="form-grid">
            <div className="form-field">
              <label>Name *</label>
              <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />
            </div>
            <div className="form-field">
              <label>Description</label>
              <input
                value={form.description}
                onChange={(e) => setForm({ ...form, description: e.target.value })}
              />
            </div>
          </div>
          <div className="form-actions">
            <button className="btn btn-primary" type="submit">
              {editingId ? "Update" : "Add"}
            </button>
            {editingId && (
              <button type="button" className="btn btn-ghost" onClick={resetForm}>
                Cancel
              </button>
            )}
          </div>
        </form>
      </div>

      {loading ? (
        <div className="alert-empty">Loading...</div>
      ) : departments.length === 0 ? (
        <div className="alert-empty">No departments yet. Add the first one above.</div>
      ) : (
        <div className="table-wrap">
          <table className="patient-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Description</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {departments.map((d) => (
                <tr key={d.id}>
                  <td>{d.name}</td>
                  <td>{d.description || "-"}</td>
                  <td>
                    <div className="row-actions">
                      <button className="btn btn-ghost btn-sm" onClick={() => startEdit(d)}>Edit</button>
                      <button className="btn btn-danger btn-sm" onClick={() => handleDelete(d)}>Delete</button>
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

export default DepartmentsPage;
