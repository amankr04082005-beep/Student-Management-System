// Talks to the Spring Boot REST API using fetch().
const form = document.getElementById("student-form");
const formTitle = document.getElementById("form-title");
const nameInput = document.getElementById("name");
const emailInput = document.getElementById("email");
const phoneInput = document.getElementById("phone");
const courseSelect = document.getElementById("course");
const marksInput = document.getElementById("marks");
const saveBtn = document.getElementById("save-btn");
const cancelBtn = document.getElementById("cancel-btn");
const messageEl = document.getElementById("message");
const rowsEl = document.getElementById("student-rows");
const emptyEl = document.getElementById("empty");
const searchInput = document.getElementById("search");
const countsEl = document.getElementById("course-counts");

let editingId = null; // null = adding a new student, number = editing that student

// Common helper for all API calls
async function request(url, options) {
  const response = await fetch(url, {
    headers: { "Content-Type": "application/json" },
    ...options,
  });
  if (response.status === 204) {
    return null;
  }
  const data = await response.json().catch(() => ({}));
  if (!response.ok) {
    throw new Error(data.error || "Something went wrong");
  }
  return data;
}

function showMessage(text, type) {
  messageEl.textContent = text;
  messageEl.className = "message " + (type || "");
}

async function loadCourses() {
  const courses = await request("/api/courses");
  courseSelect.innerHTML = "";
  courses.forEach((course) => {
    const option = document.createElement("option");
    option.value = course.id;
    option.textContent = course.name;
    courseSelect.appendChild(option);
  });
}

async function loadStudents() {
  const search = searchInput.value.trim();
  const url = search ? "/api/students?search=" + encodeURIComponent(search) : "/api/students";
  const students = await request(url);
  renderStudents(students);
}

function renderStudents(students) {
  rowsEl.innerHTML = "";
  emptyEl.classList.toggle("hidden", students.length > 0);

  students.forEach((student) => {
    const row = document.createElement("tr");

    // textContent is used (not innerHTML) so user data can never run as HTML/JS
    [student.id, student.name, student.email, student.phone || "-",
     student.courseName, student.marks.toFixed(2)].forEach((value) => {
      const cell = document.createElement("td");
      cell.textContent = value;
      row.appendChild(cell);
    });

    const actions = document.createElement("td");
    actions.className = "actions";

    const editBtn = document.createElement("button");
    editBtn.textContent = "Edit";
    editBtn.className = "small";
    editBtn.addEventListener("click", () => startEdit(student));

    const deleteBtn = document.createElement("button");
    deleteBtn.textContent = "Delete";
    deleteBtn.className = "small danger";
    deleteBtn.addEventListener("click", () => deleteStudent(student));

    actions.appendChild(editBtn);
    actions.appendChild(deleteBtn);
    row.appendChild(actions);
    rowsEl.appendChild(row);
  });
}

async function loadCourseCounts() {
  const counts = await request("/api/reports/course-count");
  countsEl.innerHTML = "";
  counts.forEach((item) => {
    const li = document.createElement("li");
    li.textContent = item.courseName + ": " + item.total;
    countsEl.appendChild(li);
  });
}

function startEdit(student) {
  editingId = student.id;
  nameInput.value = student.name;
  emailInput.value = student.email;
  phoneInput.value = student.phone || "";
  courseSelect.value = student.courseId;
  marksInput.value = student.marks;
  formTitle.textContent = "Edit Student (ID " + student.id + ")";
  saveBtn.textContent = "Update Student";
  cancelBtn.classList.remove("hidden");
  showMessage("", "");
  window.scrollTo({ top: 0, behavior: "smooth" });
}

function resetForm() {
  editingId = null;
  form.reset();
  formTitle.textContent = "Add Student";
  saveBtn.textContent = "Add Student";
  cancelBtn.classList.add("hidden");
}

async function deleteStudent(student) {
  if (!confirm("Delete " + student.name + "?")) {
    return;
  }
  try {
    await request("/api/students/" + student.id, { method: "DELETE" });
    showMessage("Student deleted.", "success");
    await refresh();
  } catch (error) {
    showMessage(error.message, "error");
  }
}

form.addEventListener("submit", async (event) => {
  event.preventDefault();

  const body = {
    name: nameInput.value.trim(),
    email: emailInput.value.trim(),
    phone: phoneInput.value.trim() === "" ? null : phoneInput.value.trim(),
    courseId: Number(courseSelect.value),
    marks: marksInput.value === "" ? null : Number(marksInput.value),
  };

  try {
    if (editingId === null) {
      await request("/api/students", { method: "POST", body: JSON.stringify(body) });
      showMessage("Student added successfully.", "success");
    } else {
      await request("/api/students/" + editingId, { method: "PUT", body: JSON.stringify(body) });
      showMessage("Student updated successfully.", "success");
    }
    resetForm();
    await refresh();
  } catch (error) {
    showMessage(error.message, "error");
  }
});

cancelBtn.addEventListener("click", () => {
  resetForm();
  showMessage("", "");
});

// Search while typing (waits 300 ms after the last key press)
let searchTimer;
searchInput.addEventListener("input", () => {
  clearTimeout(searchTimer);
  searchTimer = setTimeout(() => {
    loadStudents().catch((error) => showMessage(error.message, "error"));
  }, 300);
});

async function refresh() {
  await loadStudents();
  await loadCourseCounts();
}

// Runs once when the page opens
(async function init() {
  try {
    await loadCourses();
    await refresh();
  } catch (error) {
    showMessage("Could not load data: " + error.message, "error");
  }
})();
