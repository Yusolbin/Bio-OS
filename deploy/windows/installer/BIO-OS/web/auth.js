let currentUser = loadCurrentUser();

const authUsernameInput = document.getElementById("authUsernameInput");
const authPasswordInput = document.getElementById("authPasswordInput");
const registerButton = document.getElementById("registerButton");
const loginButton = document.getElementById("loginButton");
const logoutButton = document.getElementById("logoutButton");
const goDashboardButton = document.getElementById("goDashboardButton");
const authMessageBox = document.getElementById("authMessageBox");

registerButton.addEventListener("click", () => {
    registerUser();
});

loginButton.addEventListener("click", () => {
    loginUser();
});

logoutButton.addEventListener("click", () => {
    logoutUser();
});

goDashboardButton.addEventListener("click", () => {
    goToDashboard();
});

async function registerUser() {
    const username = authUsernameInput.value.trim();
    const password = authPasswordInput.value;

    if (!username || !password) {
        authMessageBox.textContent = "Username과 Password를 입력해 주세요.";
        return;
    }

    try {
        const response = await fetch("http://localhost:8080/api/auth/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                username: username,
                password: password,
            }),
        });

        if (!response.ok) {
            throw new Error("Register request failed: " + response.status);
        }

        const result = await response.json();

        authMessageBox.textContent = JSON.stringify(result, null, 2);

        if (result.success) {
            saveCurrentUser(result);
            renderAuthState();

            setTimeout(() => {
                window.location.href = "index.html";
            }, 500);
        }

    } catch (error) {
        console.error(error);
        authMessageBox.textContent = "Register 실패. Spring Boot 서버가 켜져 있는지 확인해 주세요.";
    }
}

async function loginUser() {
    const username = authUsernameInput.value.trim();
    const password = authPasswordInput.value;

    if (!username || !password) {
        authMessageBox.textContent = "Username과 Password를 입력해 주세요.";
        return;
    }

    try {
        const response = await fetch("http://localhost:8080/api/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                username: username,
                password: password,
            }),
        });

        if (!response.ok) {
            throw new Error("Login request failed: " + response.status);
        }

        const result = await response.json();

        authMessageBox.textContent = JSON.stringify(result, null, 2);

        if (result.success) {
            saveCurrentUser(result);
            renderAuthState();

            setTimeout(() => {
                window.location.href = "index.html";
            }, 500);
        }

    } catch (error) {
        console.error(error);
        authMessageBox.textContent = "Login 실패. Spring Boot 서버가 켜져 있는지 확인해 주세요.";
    }
}

function logoutUser() {
    localStorage.removeItem("bioOsCurrentUser");
    localStorage.removeItem("bioOsJwtToken");

    currentUser = null;

    authPasswordInput.value = "";
    authMessageBox.textContent = "Logout successful.";

    renderAuthState();
}

function goToDashboard() {
    if (!currentUser) {
        authMessageBox.textContent = "Dashboard에 들어가려면 먼저 로그인해 주세요.";
        return;
    }

    window.location.href = "index.html";
}

function saveCurrentUser(user) {
    currentUser = {
        userId: user.userId,
        username: user.username,
        role: user.role,
        token: user.token,
    };

    localStorage.setItem("bioOsCurrentUser", JSON.stringify(currentUser));

    if (user.token) {
        localStorage.setItem("bioOsJwtToken", user.token);
    }
}

function loadCurrentUser() {
    const savedUser = localStorage.getItem("bioOsCurrentUser");

    if (!savedUser) {
        return null;
    }

    try {
        return JSON.parse(savedUser);
    } catch (error) {
        localStorage.removeItem("bioOsCurrentUser");
        localStorage.removeItem("bioOsJwtToken");
        return null;
    }
}

function renderAuthState() {
    if (!currentUser) {
        authMessageBox.textContent = "Not logged in.";
        return;
    }

    const tokenStatus = currentUser.token ? "JWT issued." : "JWT missing.";

    authMessageBox.textContent =
        `${currentUser.role} account is signed in. ${tokenStatus}`;
}

renderAuthState();