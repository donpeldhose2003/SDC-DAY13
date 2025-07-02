const baseUrl = 'http://localhost:8888';

function login() {
  fetch(`${baseUrl}/login`, {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({
      email: document.getElementById('loginEmail').value,
      password: document.getElementById('loginPass').value
    })
  }).then(res => res.json()).then(data => {
    localStorage.setItem('token', data.token);
    window.location.href = "todos.html";
  }).catch(err => alert("Login failed"));
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

function loadTodos() {
  fetch(`${baseUrl}/todos`, {
    headers: {
      Authorization: 'Bearer ' + localStorage.getItem('token')
    }
  })
    .then(res => res.json())
    .then(todos => {
      const ul = document.getElementById('todoList');
      ul.innerHTML = '';
      todos.forEach(todo => {
        const li = document.createElement('li');
        li.textContent = todo.title;
        ul.appendChild(li);
      });
    });
}

function addTodo() {
  const title = document.getElementById('newTodo').value;
  fetch(`${baseUrl}/todos`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: 'Bearer ' + localStorage.getItem('token')
    },
    body: JSON.stringify({ title })
  }).then(() => {
    document.getElementById('newTodo').value = '';
    loadTodos();
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
