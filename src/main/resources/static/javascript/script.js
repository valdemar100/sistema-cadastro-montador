/**
 * script.js
 * Login form behaviour:
 * - client-side validation
 * - async login via fetch with timeout (AbortController)
 * - accessibility: aria-live updates and focus management
 * - UX: loading state, disable controls, friendly messages
 *
 * Expected server responses (JSON):
 * - Success: { success: true, message: "OK", redirect: "/app", token: "..." }
 * - Error:   { success: false, message: "Credenciais inválidas", errors: { email: "...", password: "..." } }
 */
console.log("Script de login carregado com sucesso!");
(() => {
  'use strict';

  // ------- CONFIG -------
  const LOGIN_ENDPOINT = '/api/login';
  const FETCH_TIMEOUT_MS = 10000; // 10s timeout
  const formSelector = '#loginForm'; // certifique-se de que seu <form> tenha id="loginForm"
  const emailSelector = '#email';
  const passwordSelector = '#senha';
  const submitSelector = '.btn-login';
  const messageContainerSelector = '#formMessage'; // container para mensagens (role="alert", aria-live)
  // ----------------------

  // Helper: safe text insertion to avoid XSS
  function setText(node, text) {
    if (!node) return;
    node.textContent = text;
  }

  // Helper: get CSRF token from meta tag if present
  function getCsrfToken() {
    const meta = document.querySelector('meta[name="csrf-token"]');
    return meta ? meta.getAttribute('content') : null;
  }

  // Validate inputs; returns object { isValid: boolean, errors: { field: message } }
  function validateFormValues(values) {
    const errors = {};

    // email/username: required and minimum length
    if (!values.email) {
      errors.email = 'Informe seu usuário ou e-mail.';
    } else if (values.email.length < 3) {
      errors.email = 'Informe ao menos 3 caracteres.';
    }

    // password: required and minimum length
    if (!values.password) {
      errors.password = 'Informe sua senha.';
    } else if (values.password.length < 6) {
      errors.password = 'A senha deve ter ao menos 6 caracteres.';
    }

    return { isValid: Object.keys(errors).length === 0, errors };
  }

  // Show validation errors near inputs and focus first invalid
  function showFieldErrors(formEl, errors) {
    clearFieldErrors(formEl);

    const firstInvalidField = Object.keys(errors)[0];
    for (const [field, msg] of Object.entries(errors)) {
      const input = formEl.querySelector(`#${field}`);
      if (!input) continue;

      // create or reuse an error element
      let err = input.parentElement.querySelector('.input-error');
      if (!err) {
        err = document.createElement('div');
        err.className = 'input-error';
        err.setAttribute('aria-live', 'polite');
        err.style.color = '#b00020';
        err.style.fontSize = '0.85rem';
        err.style.marginTop = '0.35rem';
        input.parentElement.appendChild(err);
      }
      setText(err, msg);

      // mark field as invalid for accessibility
      input.setAttribute('aria-invalid', 'true');
      input.setAttribute('aria-describedby', err.id || '');
    }

    // focus the first invalid field
    const firstInput = formEl.querySelector(`#${firstInvalidField}`);
    if (firstInput) {
      firstInput.focus();
    }
  }

  function clearFieldErrors(formEl) {
    formEl.querySelectorAll('.input-error').forEach(el => el.remove());
    formEl.querySelectorAll('input[aria-invalid]').forEach(inp => {
      inp.removeAttribute('aria-invalid');
      inp.removeAttribute('aria-describedby');
    });
  }

  // Display message in the top message container (role="alert")
  function showFormMessage(container, text, type = 'info') {
    if (!container) return;
    container.className = 'form-message ' + type; // allows styling by type: success, error, info
    setText(container, text);
    container.style.display = 'block';
  }

  function clearFormMessage(container) {
    if (!container) return;
    container.className = 'form-message';
    setText(container, '');
    container.style.display = 'none';
  }

  // Toggle UI loading state
  function setLoadingState(formEl, isLoading) {
    const submitBtn = formEl.querySelector(submitSelector);
    const inputs = formEl.querySelectorAll('input');

    if (isLoading) {
      if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.setAttribute('aria-disabled', 'true');
        submitBtn.dataset.originalText = submitBtn.textContent;
        submitBtn.textContent = 'Entrando...';
      }
      inputs.forEach(i => i.setAttribute('disabled', 'true'));
    } else {
      if (submitBtn) {
        submitBtn.disabled = false;
        submitBtn.removeAttribute('aria-disabled');
        if (submitBtn.dataset.originalText) {
          submitBtn.textContent = submitBtn.dataset.originalText;
        }
      }
      inputs.forEach(i => i.removeAttribute('disabled'));
    }
  }

  // Abortable fetch with timeout
  async function fetchWithTimeout(url, options = {}, timeout = FETCH_TIMEOUT_MS) {
    const controller = new AbortController();
    const id = setTimeout(() => controller.abort(), timeout);
    try {
      const response = await fetch(url, { ...options, signal: controller.signal });
      clearTimeout(id);
      return response;
    } catch (err) {
      clearTimeout(id);
      throw err;
    }
  }

  // Main submit handler
  async function handleSubmit(event) {
    event.preventDefault();
    const formEl = event.currentTarget;
    const msgContainer = document.querySelector(messageContainerSelector);

    clearFormMessage(msgContainer);
    clearFieldErrors(formEl);

    // Gather and sanitize values
    const emailInput = formEl.querySelector(emailSelector);
    const passInput = formEl.querySelector(passwordSelector);
    const email = (emailInput && emailInput.value || '').trim();
    const password = (passInput && passInput.value || '').trim();

    // Validate
    const { isValid, errors } = validateFormValues({ email, password });
    if (!isValid) {
      showFieldErrors(formEl, errors);
      showFormMessage(msgContainer, 'Revise os campos destacados.', 'error');
      return;
    }

    // Prepare payload
    const payload = { email, senha: password};

    // UX: set loading
    setLoadingState(formEl, true);

    try {
      const csrfToken = getCsrfToken();
      const headers = {
        'Content-Type': 'application/json'
      };
      if (csrfToken) headers['X-CSRF-Token'] = csrfToken;

      const res = await fetchWithTimeout(LOGIN_ENDPOINT, {
        method: 'POST',
        credentials: 'same-origin', // ajuste conforme necessário
        headers,
        body: JSON.stringify(payload)
      }, FETCH_TIMEOUT_MS);

      if (!res.ok) {
        // handle 4xx/5xx
        let text = `Erro de rede (${res.status})`;
        try {
          const json = await res.json();
          text = json.message || text;
        } catch (e) {
          // ignore json parse error
        }
        throw new Error(text);
      }

      const data = await res.json();

      if (data.success) {
        // Optionally: store token, redirect, etc.
        showFormMessage(msgContainer, data.message || 'Login realizado com sucesso.', 'success');

        // if server returns redirect path, go there
        if (data.redirect) {
          // small delay to let user read success
          setTimeout(() => { window.location.href = data.redirect; }, 700);
        } else {
          // default action: reload or redirect to homepage
          setTimeout(() => { window.location.reload(); }, 900);
        }
      } else {
        // server-side validation errors
        if (data.errors && typeof data.errors === 'object') {
          showFieldErrors(formEl, data.errors);
        }
        showFormMessage(msgContainer, data.message || 'Falha ao autenticar. Verifique suas credenciais.', 'error');
      }
    } catch (err) {
      // Differentiate abort vs network vs other errors
      if (err.name === 'AbortError') {
        showFormMessage(document.querySelector(messageContainerSelector), 'A requisição expirou. Tente novamente.', 'error');
      } else {
        showFormMessage(document.querySelector(messageContainerSelector), err.message || 'Erro de rede. Tente novamente.', 'error');
      }
      console.error('Login error:', err);
    } finally {
      setLoadingState(formEl, false);
    }
  }

  // Init function: wire up events and accessibility helpers
  function init() {
    const formEl = document.querySelector(formSelector);
    if (!formEl) {
      console.warn('Login form not found:', formSelector);
      return;
    }

    // Ensure message container exists; create if not
    let msgContainer = document.querySelector(messageContainerSelector);
    if (!msgContainer) {
      msgContainer = document.createElement('div');
      msgContainer.id = messageContainerSelector.replace('#', '');
      msgContainer.setAttribute('role', 'alert');
      msgContainer.setAttribute('aria-live', 'assertive');
      msgContainer.style.minHeight = '1.2em';
      msgContainer.style.marginBottom = '0.75rem';
      formEl.prepend(msgContainer);
    }

    // Add submit handler
    formEl.addEventListener('submit', handleSubmit);

    // Add nicer focus styles for keyboard users (optional)
    const inputs = formEl.querySelectorAll('input');
    inputs.forEach(input => {
      input.addEventListener('focus', () => {
        input.style.boxShadow = '0 0 0 4px rgba(21, 101, 192, 0.12)';
        input.style.borderColor = '#1565c0';
      });
      input.addEventListener('blur', () => {
        input.style.boxShadow = '';
        input.style.borderColor = '';
      });

      // Allow Enter to submit from inputs
      input.addEventListener('keydown', (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
          // find the form and submit
          e.preventDefault();
          formEl.requestSubmit();
        }
      });
    });

    // Improve accessibility: move focus to first input
    const firstInput = formEl.querySelector('input');
    if (firstInput) firstInput.focus();
  }

  // Start when DOM is ready
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }

})();