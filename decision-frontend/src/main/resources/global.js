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

async function loadLang(lang) {
  try {
    const res = await fetch(`language.${lang}.json`);
    _translations = await res.json();
    localStorage.setItem('dss-lang', lang);
    applyTranslations();
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
  // event.persisted означає, що сторінка дістається з кешу пам'яті
  if (event.persisted) {
    initTheme();
    // Для мови: беремо актуальну з пам'яті
    const savedLang = localStorage.getItem('dss-lang') || 'uk';
    loadLang(savedLang);
    updateLangBtn(savedLang);
  }
});