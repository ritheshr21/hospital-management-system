import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import AppLayout from "../../components/AppLayout";
import { createMedicine, listMedicines, restockMedicine } from "../../api/pharmacy";
import "../../styles/patients.css";
import "../../styles/pharmacy.css";

const emptyMedicine = {
  name: "",
  brand: "",
  category: "",
  unit: "TABLET",
  price: "",
  stockQuantity: 0,
  reorderLevel: 10
};

const UNITS = ["TABLET", "CAPSULE", "SYRUP", "INJECTION", "OINTMENT", "DROPS"];

function PharmacyDashboard() {
  const navigate = useNavigate();
  const [medicines, setMedicines] = useState([]);
  const [search, setSearch] = useState("");
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState(emptyMedicine);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  const load = async (params) => {
    setLoading(true);
    setError("");
    try {
      setMedicines(await listMedicines(params));
    } catch (e) {
      setError(e.response?.data?.message || "Failed to load medicines");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load({});
  }, []);

  const lowStock = medicines.filter((m) => m.lowStock);
  const totalUnits = medicines.reduce((sum, m) => sum + (m.stockQuantity || 0), 0);

  const handleRestock = async (m) => {
    const qty = window.prompt(`Restock "${m.name}" — how many units?`, "50");
    if (!qty) return;
    try {
      await restockMedicine(m.id, Number(qty));
      load({ search });
    } catch (e) {
      setError(e.response?.data?.message || "Restock failed");
    }
  };

  const handleCreate = async (e) => {
    e.preventDefault();
    setError("");
    try {
      await createMedicine({
        ...form,
        price: form.price === "" ? null : Number(form.price),
        stockQuantity: Number(form.stockQuantity),
        reorderLevel: Number(form.reorderLevel)
      });
      setForm(emptyMedicine);
      setShowForm(false);
      load({ search });
    } catch (e) {
      setError(e.response?.data?.message || "Failed to add medicine");
    }
  };

  return (
    <AppLayout
      title="Pharmacy"
      actions={
        <>
          <button className="btn btn-ghost" onClick={() => navigate("/prescriptions")}>Prescriptions</button>
          <button className="btn btn-primary" onClick={() => setShowForm((s) => !s)}>
            {showForm ? "Close" : "+ Add Medicine"}
          </button>
        </>
      }
    >
      {error && <div className="alert alert-error">{error}</div>}

      {lowStock.length > 0 && (
        <div className="alert-banner">
          <div className="icon">⚠️</div>
          <div>
            <div className="title">{lowStock.length} medicine{lowStock.length > 1 ? "s" : ""} at or below reorder level</div>
            <div className="body">Restock these before they run out. Alerts are also pushed to notifications.</div>
            <div className="chips">
              {lowStock.map((m) => (
                <span className="alert-chip" key={m.id}>{m.name} — {m.stockQuantity} left</span>
              ))}
            </div>
          </div>
        </div>
      )}

      <div className="stat-row">
        <div className="stat">
          <div className="value">{medicines.length}</div>
          <div className="label">Medicines</div>
        </div>
        <div className="stat">
          <div className="value">{totalUnits}</div>
          <div className="label">Units in stock</div>
        </div>
        <div className={`stat ${lowStock.length ? "danger" : ""}`}>
          <div className="value">{lowStock.length}</div>
          <div className="label">Need restocking</div>
        </div>
      </div>

      {showForm && (
        <div className="panel" style={{ marginBottom: 24 }}>
          <h3 className="form-section-title" style={{ marginTop: 0 }}>Add Medicine</h3>
          <form onSubmit={handleCreate}>
            <div className="form-grid">
              <div className="form-field">
                <label>Name *</label>
                <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />
              </div>
              <div className="form-field">
                <label>Brand</label>
                <input value={form.brand} onChange={(e) => setForm({ ...form, brand: e.target.value })} />
              </div>
              <div className="form-field">
                <label>Category</label>
                <input value={form.category} onChange={(e) => setForm({ ...form, category: e.target.value })} placeholder="e.g. Antibiotic" />
              </div>
              <div className="form-field">
                <label>Unit</label>
                <select value={form.unit} onChange={(e) => setForm({ ...form, unit: e.target.value })}>
                  {UNITS.map((u) => <option key={u} value={u}>{u}</option>)}
                </select>
              </div>
              <div className="form-field">
                <label>Price</label>
                <input type="number" min="0" step="0.01" value={form.price} onChange={(e) => setForm({ ...form, price: e.target.value })} />
              </div>
              <div className="form-field">
                <label>Opening stock</label>
                <input type="number" min="0" value={form.stockQuantity} onChange={(e) => setForm({ ...form, stockQuantity: e.target.value })} />
              </div>
              <div className="form-field">
                <label>Reorder level</label>
                <input type="number" min="0" value={form.reorderLevel} onChange={(e) => setForm({ ...form, reorderLevel: e.target.value })} />
              </div>
            </div>
            <div className="form-actions">
              <button className="btn btn-primary" type="submit">Add Medicine</button>
            </div>
          </form>
        </div>
      )}

      <form className="toolbar" onSubmit={(e) => { e.preventDefault(); load({ search }); }}>
        <input placeholder="Search by name, brand or category..." value={search} onChange={(e) => setSearch(e.target.value)} />
        <button className="btn btn-primary" type="submit">Search</button>
        <button type="button" className="btn btn-ghost" onClick={() => load({ lowStock: true })}>Low stock only</button>
        {search && (
          <button type="button" className="btn btn-ghost" onClick={() => { setSearch(""); load({}); }}>Clear</button>
        )}
      </form>

      {loading ? (
        <div className="alert-empty">Loading...</div>
      ) : medicines.length === 0 ? (
        <div className="alert-empty">No medicines yet. Add the first one.</div>
      ) : (
        <div className="table-wrap">
          <table className="patient-table">
            <thead>
              <tr>
                <th>Medicine</th>
                <th>Brand</th>
                <th>Category</th>
                <th>Unit</th>
                <th>Stock</th>
                <th>Reorder at</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {medicines.map((m) => (
                <tr key={m.id}>
                  <td>{m.name}</td>
                  <td>{m.brand || "-"}</td>
                  <td>{m.category || "-"}</td>
                  <td>{m.unit || "-"}</td>
                  <td><span className={`stock-pill ${m.lowStock ? "stock-low" : "stock-ok"}`}>{m.stockQuantity}</span></td>
                  <td>{m.reorderLevel}</td>
                  <td>
                    <button className="btn btn-ghost btn-sm" onClick={() => handleRestock(m)}>Restock</button>
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

export default PharmacyDashboard;
