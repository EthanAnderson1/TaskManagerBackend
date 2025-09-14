import React, { useState, useEffect, useCallback } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate, Link, useNavigate } from "react-router-dom";

// Single-file React app. Tailwind CSS expected in the host project.
// Default export a React component that renders the whole app.

// --- API helpers (change base URL to your backend) ---
const API_BASE = import.meta.env.VITE_API_BASE || "";

async function apiRequest(path, method = "GET", body, token) {
  const opts = {
    method,
    headers: {
      "Content-Type": "application/json",
    },
  };
  if (token) opts.headers["Authorization"] = `Bearer ${token}`;
  if (body) opts.body = JSON.stringify(body);

  const res = await fetch(`${API_BASE}${path}`, opts);

  // try to parse json safely
  let payloadText = null;
  try {
    payloadText = await res.text();
    const maybeJson = payloadText ? JSON.parse(payloadText) : null;
    if (!res.ok) {
      const errMsg = (maybeJson && (maybeJson.message || maybeJson.error)) || payloadText || res.statusText;
      throw new Error(errMsg);
    }
    return maybeJson;
  } catch (err) {
    // if parsing failed but status ok, return raw text
    if (res.ok) return payloadText;
    throw new Error(err.message || "Request failed");
  }
}

export default function TaskManagerApp() {
  return (
    <Router>
      <div className="min-h-screen bg-gradient-to-br from-purple-300 via-pink-200 to-yellow-200">
        <BackgroundShapes />
        <div className="relative z-10">
          <AppRoutes />
        </div>
      </div>
    </Router>
  );
}

function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<AuthPage mode="login" />} />
      <Route path="/signup" element={<AuthPage mode="signup" />} />
      <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
      <Route path="/" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
}

// ----------------- Background Shapes -----------------
function BackgroundShapes() {
  return (
    <svg className="pointer-events-none fixed inset-0 w-full h-full" xmlns="http://www.w3.org/2000/svg">
      <defs>
        <linearGradient id="g1" x1="0" x2="1">
          <stop offset="0" stopColor="#ff7ab6" />
          <stop offset="1" stopColor="#7a6bff" />
        </linearGradient>
      </defs>
      <g opacity="0.12">
        <rect x="-10%" y="5%" width="40%" height="40%" fill="url(#g1)" transform="rotate(20 0 0)" rx="24" />
        <circle cx="85%" cy="10%" r="200" fill="#fff1" />
        <ellipse cx="10%" cy="90%" rx="220" ry="120" fill="#ffffff22" />
      </g>
    </svg>
  );
}

// ----------------- Auth Page -----------------
function AuthPage({ mode }) {
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    document.title = mode === "login" ? "Login — Tasks" : "Sign Up — Tasks";
  }, [mode]);

  async function handleSubmit(e) {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      if (mode === "login") {
        const data = await apiRequest("/login", "POST", { username, password });
        // backend may return { token } or { data: { token } }
        const token = data || (data.token || (data.data && data.data.token));
        if (!token) throw new Error("No token returned from server");
        localStorage.setItem("token", token);
        navigate("/dashboard");
      } else {
        await apiRequest("/signup", "POST", { username, password });
        // after signup, redirect to login
        navigate("/login");
      }
    } catch (err) {
      setError(err.message || "Unknown error");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="flex items-center justify-center min-h-screen p-6">
      <div className="w-full max-w-md bg-white/80 backdrop-blur-md rounded-2xl shadow-xl p-8">
        <h1 className="text-2xl font-bold mb-2">{mode === "login" ? "Welcome back" : "Create an account"}</h1>
        <p className="text-sm text-gray-600 mb-6">{mode === "login" ? "Log in to manage your tasks" : "Sign up to start tracking tasks"}</p>
        <form onSubmit={handleSubmit} className="space-y-4">
          <label className="block">
            <span className="text-sm font-medium">Username</span>
            <input required value={username} onChange={(e) => setUsername(e.target.value)} className="mt-1 block w-full rounded-lg border border-gray-200 p-2" />
          </label>
          <label className="block">
            <span className="text-sm font-medium">Password</span>
            <input required type="password" value={password} onChange={(e) => setPassword(e.target.value)} className="mt-1 block w-full rounded-lg border border-gray-200 p-2" />
          </label>
          {error && <div className="text-sm text-red-600">{error}</div>}
          <div className="flex items-center justify-between">
            <button disabled={loading} className="px-4 py-2 bg-indigo-600 text-white rounded-lg shadow">{loading ? "Please wait..." : (mode === "login" ? "Login" : "Sign up")}</button>
            <Link className="text-sm text-indigo-700 hover:underline" to={mode === "login" ? "/signup" : "/login"}>{mode === "login" ? "Create account" : "Already have an account?"}</Link>
          </div>
        </form>
      </div>
    </div>
  );
}

// ----------------- Protected Route -----------------
function ProtectedRoute({ children }) {
  const token = localStorage.getItem("token");
  if (!token) return <Navigate to="/login" replace />;
  return children;
}

// ----------------- Dashboard -----------------
const STATUS = ["OPEN", "INPROGRESS", "CLOSED", "BLOCKED"];
const PRIORITY = ["HIGH", "MEDIUM", "LOW"];

function Dashboard() {
  const navigate = useNavigate();
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [status, setStatus] = useState(STATUS[0]);
  const [priority, setPriority] = useState(PRIORITY[1]);

  // Filters
  const [filterStatus, setFilterStatus] = useState("All");
  const [filterPriority, setFilterPriority] = useState("All");
  const [search, setSearch] = useState("");

  const [editingTask, setEditingTask] = useState(null);

  const token = localStorage.getItem("token");

  // loadTasks defined with useCallback so it is always available and stable
  const loadTasks = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await apiRequest("/tasks", "GET", null, token);
      // backend may return { tasks: [...] } or an array directly
      const payload = data && (Array.isArray(data) ? data : (data.tasks || data.data || []));
      console.log("payload ", payload);
      setTasks(payload || []);
    } catch (err) {
      setError(err.message || "Failed to load tasks");
      if (err.message && err.message.toLowerCase().includes("unauth")) {
        localStorage.removeItem("token");
        navigate("/login");
      }
    } finally {
      setLoading(false);
    }
    // include navigate and token as dependencies
  }, [navigate, token]);

  useEffect(() => {
    document.title = "Dashboard — Tasks";
    loadTasks();
  }, [loadTasks]);

  async function handleCreate(e) {
    e.preventDefault();
    try {
      const newTask = { title, description, status, priority };
      const created = await apiRequest("/tasks", "POST", newTask, token);
      const createdTask = created && (created.task || created.data || created) || null;
      // fallback: if backend didn't return task, create a local one
      const finalTask = createdTask || { id: `tmp-${Date.now()}`, title, description, status, priority };
      setTasks((s) => [finalTask, ...s]);
      setTitle("");
      setDescription("");
      setStatus(STATUS[0]);
      setPriority(PRIORITY[1]);
    } catch (err) {
      alert("Failed to create task: " + err.message);
    }
  }

  async function handleUpdate(updated) {
    try {
      // try to update on server (if id looks like a server id)
      if (updated.id && !String(updated.id).startsWith("tmp-")) {
        const res = await apiRequest(`/tasks/${updated.id}`, "PUT", updated, token);
        const resTask = res && (res.task || res.data || res) || updated;
        setTasks((s) => s.map((t) => (t.id === resTask.id ? resTask : t)));
      } else {
        // local-only update
        setTasks((s) => s.map((t) => (t.id === updated.id ? updated : t)));
      }
      setEditingTask(null);
    } catch (err) {
      alert("Failed to update task: " + err.message);
    }
  }

  function logout() {
    localStorage.removeItem("token");
    navigate("/login");
  }

  // Filtering and searching
  const visible = tasks.filter((t) => {
    if (!t) return false;
    if (filterStatus !== "All" && String(t.status) !== filterStatus) return false;
    if (filterPriority !== "All" && String(t.priority) !== filterPriority) return false;
    if (search && !(t.title || "").toLowerCase().includes(search.toLowerCase())) return false;
    return true;
  });

  // Group by status (guaranteed keys for the four statuses)
  const grouped = STATUS.reduce((acc, s) => {
    acc[s] = visible.filter((t) => String(t.status) === s);
    return acc;
  }, {});

  return (
    <div className="p-6">
      <header className="flex items-center justify-between mb-6">
        <h2 className="text-3xl font-extrabold">Tasks Dashboard</h2>
        <div className="flex items-center gap-3">
          <button onClick={logout} className="px-3 py-2 rounded-lg border">Logout</button>
        </div>
      </header>

      <section className="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-8">
        <div className="col-span-1 lg:col-span-2 bg-white/80 p-4 rounded-2xl shadow">
          <h3 className="font-semibold mb-2">Create Task</h3>
          <form onSubmit={handleCreate} className="space-y-3">
            <input required placeholder="Title" value={title} onChange={(e) => setTitle(e.target.value)} className="w-full p-2 rounded-lg border" />
            <textarea placeholder="Description" value={description} onChange={(e) => setDescription(e.target.value)} className="w-full p-2 rounded-lg border" />
            <div className="flex gap-2">
              <select value={status} onChange={(e) => setStatus(e.target.value)} className="p-2 rounded-lg border">
                {STATUS.map((s) => <option key={s} value={s}>{s}</option>)}
              </select>
              <select value={priority} onChange={(e) => setPriority(e.target.value)} className="p-2 rounded-lg border">
                {PRIORITY.map((p) => <option key={p} value={p}>{p}</option>)}
              </select>
              <button className="px-3 py-2 bg-indigo-600 text-white rounded-lg">Add</button>
            </div>
          </form>
        </div>

        <div className="bg-white/80 p-4 rounded-2xl shadow">
          <h3 className="font-semibold mb-2">Filters</h3>
          <div className="space-y-2">
            <input placeholder="Search by title" value={search} onChange={(e) => setSearch(e.target.value)} className="w-full p-2 rounded-lg border" />
            <select value={filterStatus} onChange={(e) => setFilterStatus(e.target.value)} className="w-full p-2 rounded-lg border">
              <option value="All">All</option>
              {STATUS.map((s) => <option key={s} value={s}>{s}</option>)}
            </select>
            <select value={filterPriority} onChange={(e) => setFilterPriority(e.target.value)} className="w-full p-2 rounded-lg border">
              <option value="All">All</option>
              {PRIORITY.map((p) => <option key={p} value={p}>{p}</option>)}
            </select>
            <div className="flex gap-2">
              <button onClick={() => { setFilterStatus("All"); setFilterPriority("All"); setSearch(""); }} className="flex-1 p-2 border rounded-lg">Reset</button>
              <button onClick={loadTasks} className="flex-1 p-2 bg-green-500 text-white rounded-lg">Refresh</button>
            </div>
          </div>
        </div>
      </section>

      {loading && <div>Loading tasks...</div>}
      {error && <div className="text-red-600">{error}</div>}

      <section className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {STATUS.map((s) => (
          <div key={s} className="bg-white/90 rounded-2xl p-3 shadow min-h-[120px]">
            <h4 className="font-semibold mb-2 flex items-center justify-between">
              <span>{s}</span>
              <span className="text-sm text-gray-500">{(grouped[s] || []).length}</span>
            </h4>
            <div className="space-y-3 max-h-[60vh] overflow-auto pr-2">
              {(grouped[s] || []).map((t) => (
                <TaskCard key={t.id || t._id || t.tempId || `${s}-${Math.random()}`} task={t} onEdit={() => setEditingTask(t)} />
              ))}
              {(!grouped[s] || grouped[s].length === 0) && <div className="text-sm text-gray-400">No tasks</div>}
            </div>
          </div>
        ))}
      </section>

      {editingTask && <EditTaskModal task={editingTask} onClose={() => setEditingTask(null)} onSave={handleUpdate} />}
    </div>
  );
}

function TaskCard({ task, onEdit }) {
  return (
    <div className="border rounded-xl p-3 bg-gradient-to-r from-white to-white/60 shadow-sm">
      <div className="flex items-start justify-between">
        <div>
          <h5 className="font-semibold">{task.title}</h5>
          <p className="text-sm text-gray-600 truncate">{task.description}</p>
        </div>
        <div className="text-right text-sm">
          <div className="mb-1">{task.priority}</div>
          <div className="text-xs text-gray-500">ID: {task.id || task._id || '—'}</div>
        </div>
      </div>
      <div className="mt-3 flex items-center justify-between">
        <div className="text-xs px-2 py-1 rounded-full border">{task.status}</div>
        <div className="flex gap-2">
          <button onClick={onEdit} className="text-sm px-2 py-1 rounded-md border">Edit</button>
        </div>
      </div>
    </div>
  );
}

function EditTaskModal({ task, onClose, onSave }) {
  const [description, setDescription] = useState(task.description || "");
  const [status, setStatus] = useState(task.status || STATUS[0]);
  const [priority, setPriority] = useState(task.priority || PRIORITY[1]);

  function handleSave() {
    // Validate values are from allowed enums
    if (!STATUS.includes(status) || !PRIORITY.includes(priority)) {
      alert("Invalid status or priority");
      return;
    }
    onSave({ ...task, description, status, priority });
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      <div className="absolute inset-0 bg-black/40" onClick={onClose} />
      <div className="relative bg-white rounded-2xl p-6 w-full max-w-lg shadow-xl">
        <h3 className="text-xl font-semibold mb-3">Edit Task</h3>
        <label className="block mb-2">
          <span className="text-sm">Description</span>
          <textarea value={description} onChange={(e) => setDescription(e.target.value)} className="mt-1 w-full p-2 rounded-lg border" />
        </label>
        <div className="flex gap-2 mb-4">
          <select value={status} onChange={(e) => setStatus(e.target.value)} className="p-2 rounded-lg border flex-1">
            {STATUS.map((s) => <option key={s} value={s}>{s}</option>)}
          </select>
          <select value={priority} onChange={(e) => setPriority(e.target.value)} className="p-2 rounded-lg border w-36">
            {PRIORITY.map((p) => <option key={p} value={p}>{p}</option>)}
          </select>
        </div>
        <div className="flex justify-end gap-2">
          <button onClick={onClose} className="px-3 py-2 rounded-lg border">Cancel</button>
          <button onClick={handleSave} className="px-3 py-2 rounded-lg bg-indigo-600 text-white">Save</button>
        </div>
      </div>
    </div>
  );
}

// ----------------- Notes -----------------
// Backend endpoints expected:
// POST  /api/auth/signup   { username, password } => 201
// POST  /api/auth/login    { username, password } => { token }
// GET   /api/tasks         Authorization: Bearer <token> => [ tasks ] or { tasks: [...] }
// POST  /api/tasks         { title, description, status, priority } => created task
// PUT   /api/tasks/:id     { title?, description?, status?, priority? } => updated task

// If you use a local backend while developing, set REACT_APP_API_BASE in your .env to the backend base url (e.g. http://localhost:4000)

// This single-file component assumes you have Tailwind CSS available. If you don't, the classes will gracefully degrade and you can replace styling as needed.

// End of file.
