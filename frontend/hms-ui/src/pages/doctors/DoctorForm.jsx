import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import AppLayout from "../../components/AppLayout";
import { createDoctor, getDoctor, updateDoctor } from "../../api/doctors";
import { listDepartments } from "../../api/departments";
import "../../styles/patients.css";

const emptyForm = {
  name: "",
  email: "",
  phone: "",
  specialization: "",
  qualification: "",
  consultationFee: "",
  active: true,
  departmentId: ""
};

function DoctorForm() {
  const navigate = useNavigate();
  const { id } = useParams();
  const isEdit = Boolean(id);

  const [form, setForm] = useState(emptyForm);
  const [departments, setDepartments] = useState([]);
  const [error, setError] = useState("");
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    listDepartments().then(setDepartments).catch(() => {});
    if (isEdit) {
      getDoctor(id)
        .then((d) =>
          setForm({
            ...emptyForm,
            ...d,
            consultationFee: d.consultationFee ?? "",
            departmentId: d.department?.id || ""
          })
        )
        .catch((e) => setError(e.response?.data?.message || "Failed to load doctor"));
    }
  }, [id, isEdit]);

  const setField = (name, value) => setForm((f) => ({ ...f, [name]: value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSaving(true);

    const payload = {
      ...form,
      consultationFee: form.consultationFee === "" ? null : Number(form.consultationFee),
      departmentId: form.departmentId || null
    };

    try {
      if (isEdit) {
        await updateDoctor(id, payload);
        navigate(`/doctors/${id}`);
      } else {
        const created = await createDoctor(payload);
        navigate(`/doctors/${created.id}`);
      }
    } catch (e) {
      setError(e.response?.data?.message || "Failed to save doctor");
      setSaving(false);
    }
  };

  return (
    <AppLayout title={isEdit ? "Edit Doctor" : "Add Doctor"}>
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
              <label>Specialization</label>
              <input value={form.specialization} onChange={(e) => setField("specialization", e.target.value)} />
            </div>
            <div className="form-field">
              <label>Qualification</label>
              <input value={form.qualification} onChange={(e) => setField("qualification", e.target.value)} placeholder="e.g. MBBS, MD" />
            </div>
            <div className="form-field">
              <label>Consultation Fee</label>
              <input type="number" min="0" step="0.01" value={form.consultationFee} onChange={(e) => setField("consultationFee", e.target.value)} />
            </div>
            <div className="form-field">
              <label>Department</label>
              <select value={form.departmentId} onChange={(e) => setField("departmentId", e.target.value)}>
                <option value="">Unassigned</option>
                {departments.map((d) => (
                  <option key={d.id} value={d.id}>{d.name}</option>
                ))}
              </select>
            </div>
            <div className="form-field">
              <label>Status</label>
              <label style={{ display: "flex", alignItems: "center", gap: 8, marginTop: 6 }}>
                <input
                  type="checkbox"
                  checked={form.active}
                  onChange={(e) => setField("active", e.target.checked)}
                  style={{ width: "auto" }}
                />
                Active
              </label>
            </div>
          </div>

          <div className="form-actions">
            <button className="btn btn-primary" type="submit" disabled={saving}>
              {saving ? "Saving..." : isEdit ? "Update Doctor" : "Create Doctor"}
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

export default DoctorForm;
