const baseUrl = 'http://localhost:8888';

function login() {
  const email = document.getElementById("loginEmail").value;
  const password = document.getElementById("loginPass").value;

  fetch("/login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({ email, password })
  })
  .then(res => {
    if (!res.ok) throw new Error("Login failed");
    return res.json();
  })
  .then(data => {
    localStorage.setItem("token", data.token);
    window.location.href = "/web/dashboard.html";
  })
  .catch(err => alert(err.message));
}

function register() {
  fetch(`${baseUrl}/register`, {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({
      name: document.getElementById('regName').value,
      email: document.getElementById('regEmail').value
    })
  }).then(res => res.text()).then(alert);
}

function resetPassword() {
  const email = document.getElementById('resetEmail').value;
  fetch(`${baseUrl}/reset-password`, {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({ email })
  }).then(res => res.text()).then(alert);
}

function addTodo() {
    const title = document.getElementById("title").value;
    const description = document.getElementById("desc").value;
    const dueDate = document.getElementById("due").value;
    const priority = document.getElementById("priority").value;

    fetch(`${baseUrl}/todos`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + localStorage.getItem("token")
        },
        body: JSON.stringify({ title, description, dueDate, priority })
    }).then(res => {
        if (res.ok) {
            alert("Task added");
            loadTodos(); // Reload list
        } else {
            alert("Failed to add task");
        }
    });
}

function loadTodos() {
    fetch(`${baseUrl}/todos`, {
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token")
        }
    }).then(res => res.json())
    .then(data => {
        const list = document.getElementById("taskList");
        list.innerHTML = "";
        data.forEach(task => {
            const item = document.createElement("li");
            item.innerText = `${task.title} - ${task.priority} - ${task.dueDate}`;
            list.appendChild(item);
        });
    });
}

function logout() {
  fetch(`${baseUrl}/logout`, {
    method: 'POST',
    headers: {
      Authorization: 'Bearer ' + localStorage.getItem('token')
    }
  }).then(() => {
    localStorage.removeItem('token');
    window.location.href = 'index.html';
  });
}
