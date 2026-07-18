import { useEffect, useRef, useState } from "react";
import AppLayout from "../../components/AppLayout";
import { getQueue } from "../../api/appointments";
import "../../styles/appointments.css";

/**
 * Live waiting list. Polls the Redis-backed priority queue every few seconds so
 * a CRITICAL case booked later visibly jumps ahead of routine ones.
 */
function QueueBoard() {
  const [queue, setQueue] = useState([]);
  const [error, setError] = useState("");
  const [updatedAt, setUpdatedAt] = useState(null);
  const timer = useRef(null);

  const load = async () => {
    try {
      setQueue(await getQueue());
      setUpdatedAt(new Date());
      setError("");
    } catch (e) {
      setError(e.response?.data?.message || "Failed to load queue");
    }
  };

  useEffect(() => {
    load();
    timer.current = setInterval(load, 4000);
    return () => clearInterval(timer.current);
  }, []);

  return (
    <AppLayout
      title="Live Queue"
      actions={<button className="btn btn-ghost" onClick={load}>Refresh</button>}
    >
      <p className="app-subtitle" style={{ marginBottom: 18 }}>
        <span className="live-dot" />
        Auto-updating priority queue{updatedAt ? ` · updated ${updatedAt.toLocaleTimeString()}` : ""}
      </p>

      {error && <div className="alert alert-error">{error}</div>}

      {queue.length === 0 ? (
        <div className="alert-empty">The queue is empty.</div>
      ) : (
        <div className="queue-board">
          {queue.map(({ position, appointment: a }) => (
            <div key={a.id} className={`queue-item ${a.urgency >= 5 ? "crit" : ""}`}>
              <div className="queue-pos">{position}</div>
              <div className="queue-main">
                <div className="name">
                  {a.patientName} <span className={`urgency urgency-${a.urgency}`}>{a.urgencyLabel}</span>
                </div>
                <div className="sub">{a.department} · {a.doctorName}</div>
              </div>
              <div className="queue-token">Token #{a.tokenNumber}</div>
            </div>
          ))}
        </div>
      )}
    </AppLayout>
  );
}

export default QueueBoard;
