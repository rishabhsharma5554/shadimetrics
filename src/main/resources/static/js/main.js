// Shaadi Metrics — interaction layer (nav, tilt, reveal, counters, compare slider, lightbox, modal)

document.addEventListener("DOMContentLoaded", () => {
  initNavbar();
  initMobileMenu();
  initTilt();
  initReveal();
  initCounters();
  initCompareSlider();
  initLightbox();
  initGalleryFilters();
  initConsultationModal();
});

/* ---------------- Navbar ---------------- */
function initNavbar() {
  const nav = document.querySelector(".navbar");
  if (!nav) return;
  const onScroll = () => {
    if (window.scrollY > 40) nav.classList.add("scrolled");
    else nav.classList.remove("scrolled");
  };
  onScroll();
  window.addEventListener("scroll", onScroll, { passive: true });
}

function initMobileMenu() {
  const toggle = document.querySelector(".nav-toggle");
  const links = document.querySelector(".nav-links");
  if (!toggle || !links) return;
  toggle.addEventListener("click", () => {
    const isOpen = links.classList.toggle("mobile-open");
    links.style.cssText = isOpen
      ? "display:flex;position:absolute;top:100%;left:0;right:0;flex-direction:column;background:#fdf9f2;padding:24px;gap:18px;box-shadow:0 20px 40px -20px rgba(44,8,18,0.3);"
      : "";
    if (isOpen) links.querySelectorAll("a").forEach(a => (a.style.color = "#3d0d1a"));
  });
}

/* ---------------- 3D tilt on pointer move ---------------- */
function initTilt() {
  const cards = document.querySelectorAll(".tilt-card");
  cards.forEach((card) => {
    const strength = parseFloat(card.dataset.tiltStrength || "10");
    card.addEventListener("pointermove", (e) => {
      const rect = card.getBoundingClientRect();
      const x = (e.clientX - rect.left) / rect.width - 0.5;
      const y = (e.clientY - rect.top) / rect.height - 0.5;
      card.style.transform = `perspective(900px) rotateY(${x * strength}deg) rotateX(${-y * strength}deg) translateY(-6px)`;
    });
    card.addEventListener("pointerleave", () => {
      card.style.transform = "perspective(900px) rotateY(0deg) rotateX(0deg) translateY(0)";
    });
  });
}

/* ---------------- Scroll reveal ---------------- */
function initReveal() {
  const items = document.querySelectorAll(".reveal");
  if (!("IntersectionObserver" in window)) {
    items.forEach((el) => el.classList.add("in"));
    return;
  }
  const io = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          entry.target.classList.add("in");
          io.unobserve(entry.target);
        }
      });
    },
    { threshold: 0.15 }
  );
  items.forEach((el) => io.observe(el));
}

/* ---------------- Animated counters ---------------- */
function initCounters() {
  const counters = document.querySelectorAll("[data-count]");
  if (!counters.length) return;
  const animate = (el) => {
    const target = parseFloat(el.dataset.count);
    const suffix = el.dataset.suffix || "";
    const duration = 1600;
    const start = performance.now();
    const step = (now) => {
      const progress = Math.min((now - start) / duration, 1);
      const eased = 1 - Math.pow(1 - progress, 3);
      el.textContent = Math.round(target * eased) + suffix;
      if (progress < 1) requestAnimationFrame(step);
    };
    requestAnimationFrame(step);
  };
  const io = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          animate(entry.target);
          io.unobserve(entry.target);
        }
      });
    },
    { threshold: 0.6 }
  );
  counters.forEach((el) => io.observe(el));
}

/* ---------------- Before / After compare slider ---------------- */
function initCompareSlider() {
  const wrap = document.querySelector(".compare-wrap");
  if (!wrap) return;
  const after = wrap.querySelector(".compare-after");
  const handle = wrap.querySelector(".compare-handle");

  const setPosition = (clientX) => {
    const rect = wrap.getBoundingClientRect();
    let pct = ((clientX - rect.left) / rect.width) * 100;
    pct = Math.max(2, Math.min(98, pct));
    after.style.clipPath = `inset(0 0 0 ${pct}%)`;
    handle.style.left = `${pct}%`;
  };

  let dragging = false;
  wrap.addEventListener("pointerdown", (e) => { dragging = true; setPosition(e.clientX); });
  window.addEventListener("pointermove", (e) => { if (dragging) setPosition(e.clientX); });
  window.addEventListener("pointerup", () => (dragging = false));
  wrap.addEventListener("touchmove", (e) => setPosition(e.touches[0].clientX), { passive: true });
}

/* ---------------- Lightbox for gallery ---------------- */
function initLightbox() {
  const lightbox = document.querySelector(".lightbox");
  if (!lightbox) return;
  const lightboxImg = lightbox.querySelector("img");
  document.querySelectorAll(".masonry-item img").forEach((img) => {
    img.addEventListener("click", () => {
      lightboxImg.src = img.src;
      lightbox.classList.add("open");
    });
  });
  lightbox.addEventListener("click", (e) => {
    if (e.target === lightbox || e.target.classList.contains("lightbox-close")) {
      lightbox.classList.remove("open");
    }
  });
}

/* ---------------- Gallery category filters ---------------- */
function initGalleryFilters() {
  const buttons = document.querySelectorAll(".filter-btn");
  const items = document.querySelectorAll(".masonry-item");
  if (!buttons.length) return;
  buttons.forEach((btn) => {
    btn.addEventListener("click", () => {
      buttons.forEach((b) => b.classList.remove("active"));
      btn.classList.add("active");
      const cat = btn.dataset.filter;
      items.forEach((item) => {
        const show = cat === "all" || item.dataset.category === cat;
        item.style.display = show ? "" : "none";
      });
    });
  });
}

/* ---------------- Consultation modal ---------------- */
function initConsultationModal() {
  const modal = document.querySelector(".modal-backdrop");
  if (!modal) return;
  document.querySelectorAll("[data-open-modal]").forEach((btn) => {
    btn.addEventListener("click", (e) => {
      e.preventDefault();
      modal.classList.add("open");
    });
  });
  modal.addEventListener("click", (e) => {
    if (e.target === modal || e.target.classList.contains("modal-close")) {
      modal.classList.remove("open");
    }
  });
}
