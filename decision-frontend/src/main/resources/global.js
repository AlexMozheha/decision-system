// ─── THEME ────────────────────────────────────────────────────────
function initTheme() {
  const saved = localStorage.getItem('dss-theme') || 'light';
  document.documentElement.setAttribute('data-theme', saved);
  updateThemeBtn(saved);
}

function toggleTheme() {
  const current = document.documentElement.getAttribute('data-theme') || 'light';
  const next = current === 'light' ? 'dark' : 'light';
  document.documentElement.setAttribute('data-theme', next);
  localStorage.setItem('dss-theme', next);
  updateThemeBtn(next);
}

function updateThemeBtn(theme) {
  const btn = document.getElementById('btn-theme');
  if (!btn) return;
  btn.innerHTML = theme === 'dark'
    ? '<i class="ti ti-sun" aria-hidden="true"></i>'
    : '<i class="ti ti-moon" aria-hidden="true"></i>';
}

// ─── I18N ─────────────────────────────────────────────────────────
let _translations = {};

async function initLang() {
  const saved = localStorage.getItem('dss-lang') || 'uk';
  await loadLang(saved);
  updateLangBtn(saved);
}

window.isDssLangReady = false;

async function loadLang(lang) {
  try {
    const res = await fetch(`language.${lang}.json`);
    _translations = await res.json();
    localStorage.setItem('dss-lang', lang);
    applyTranslations();
    window.isDssLangReady = true;
  document.dispatchEvent(new Event("langChanged"));
  } catch (e) {
    console.error('language load error:', e);
  }
}

async function toggleLang() {
  const current = localStorage.getItem('dss-lang') || 'uk';
  const next = current === 'uk' ? 'en' : 'uk';
  await loadLang(next);
  updateLangBtn(next);
}

function updateLangBtn(lang) {
  const btn = document.getElementById('btn-lang');
  if (!btn) return;
  btn.textContent = lang === 'uk' ? 'EN' : 'УК';
}

// Повернути переклад по ключу типу "nav.logout"
function t(key) {
  const parts = key.split('.');
  let val = _translations;
  for (const p of parts) {
    if (val == null) return key;
    val = val[p];
  }
  return val ?? key;
}

// Застосувати переклади до елементів"
function applyTranslations() {
  document.querySelectorAll('[data-language]').forEach(el => {
    const key = el.getAttribute('data-language');
    const attr = el.getAttribute('data-language-attr');
    const isHtml = el.hasAttribute('data-html');
    const val = t(key);

    if (!val) return;

    if (attr) {
      el.setAttribute(attr, val);
    } 
    else if (isHtml) {
      el.innerHTML = val;}
    else {
      el.textContent = val;
    }
  });
}

// ─── INIT ─────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', async () => {
  initTheme();
  await initLang();
  getUserFromToken();
});

window.addEventListener('storage', async (event) => {
  if (event.key === 'dss-theme') {
    const newTheme = event.newValue || 'light';
    document.documentElement.setAttribute('data-theme', newTheme);
    updateThemeBtn(newTheme);
  }
  
  // Якщо інша сторінка змінила мову
  if (event.key === 'dss-lang') {
    const newLang = event.newValue || 'uk';
    await loadLang(newLang);
    updateLangBtn(newLang);
  }
});

window.addEventListener('pageshow', (event) => {
  if (event.persisted) {
    initTheme();
    const savedLang = localStorage.getItem('dss-lang') || 'uk';
    loadLang(savedLang);
    updateLangBtn(savedLang);
  }
});

// ─── API CLIENTS ──────────────────────────────────────────────────
const authInterceptor = config => {
    const token = localStorage.getItem('dss-token');
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
};

const userApi = axios.create({ baseURL: 'http://localhost:8083' });
const decisionApi = axios.create({ baseURL: 'http://localhost:8081' });

const responseErrorInterceptor = error => {
    if (error.response && (error.response.status === 401 || error.response.status === 403)) {
        
        const url = error.config.url;
        if (!url.includes('/api/auth/register') && !url.includes('/api/auth/login')) {
            console.warn('Отримано 401/403 від сервера. Токен недійсний. Очищення...');
            localStorage.removeItem('dss-token');  
        }
    }
    return Promise.reject(error);
};

[userApi, decisionApi].forEach(instance => {
    instance.interceptors.request.use(authInterceptor);
});

function getUserFromToken() {
    const token = localStorage.getItem('dss-token');
    if (!token) return null;
    
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));

        const claims = JSON.parse(jsonPayload);

        if (claims.exp && claims.exp * 1000 < Date.now()) {
            console.warn('Токен протух, видаляємо з пам\'яті...');
            localStorage.removeItem('dss-token');
            return null;
        }

        return {
            login: claims.sub || claims.login || 'Admin'
        };
    } catch (e) {
        console.error('Не вдалося розпарсити JWT токен:', e);
        localStorage.removeItem('dss-token');
        return null;
    }
}