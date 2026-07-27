// Shaadi Metrics — premium 3D hero scene (ES module, Three.js r160+).
// Two interlocked PBR gold rings, a soft glow layer, and drifting golden petals —
// all reacting to pointer movement and to scroll position as the hero leaves view.

import * as THREE from 'three';

const canvas = document.getElementById('hero-canvas');
if (canvas) {
  const scene = new THREE.Scene();
  const camera = new THREE.PerspectiveCamera(42, canvas.clientWidth / canvas.clientHeight, 0.1, 100);
  camera.position.set(0, 0, 9.5);

  const renderer = new THREE.WebGLRenderer({ canvas, alpha: true, antialias: true });
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
  renderer.toneMapping = THREE.ACESFilmicToneMapping;
  renderer.toneMappingExposure = 1.15;

  const resize = () => {
    const { clientWidth, clientHeight } = canvas;
    if (clientWidth === 0 || clientHeight === 0) return;
    renderer.setSize(clientWidth, clientHeight, false);
    camera.aspect = clientWidth / clientHeight;
    camera.updateProjectionMatrix();
  };
  resize();
  window.addEventListener('resize', resize);

  const GOLD = 0xd9b04c;
  const GOLD_LIGHT = 0xf3dd9a;

  // ---- Lighting: warm key + cool rim + soft fill, no environment map needed ----
  scene.add(new THREE.AmbientLight(0xffffff, 0.35));
  const key = new THREE.PointLight(GOLD_LIGHT, 60, 40, 2);
  key.position.set(4.5, 3.5, 6);
  scene.add(key);
  const rim = new THREE.PointLight(0x8fb4ff, 18, 40, 2);
  rim.position.set(-6, -2, 4);
  scene.add(rim);
  const fill = new THREE.PointLight(0xffffff, 10, 40, 2);
  fill.position.set(0, -4, 5);
  scene.add(fill);

  // ---- Soft glow sprite (cheap bloom substitute, keeps canvas alpha-safe) ----
  function makeGlowTexture() {
    const size = 256;
    const c = document.createElement('canvas');
    c.width = c.height = size;
    const ctx = c.getContext('2d');
    const g = ctx.createRadialGradient(size / 2, size / 2, 0, size / 2, size / 2, size / 2);
    g.addColorStop(0, 'rgba(243,221,154,0.9)');
    g.addColorStop(0.4, 'rgba(217,176,76,0.35)');
    g.addColorStop(1, 'rgba(217,176,76,0)');
    ctx.fillStyle = g;
    ctx.fillRect(0, 0, size, size);
    return new THREE.CanvasTexture(c);
  }
  const glowTex = makeGlowTexture();
  const glowMat = new THREE.SpriteMaterial({ map: glowTex, transparent: true, depthWrite: false, blending: THREE.AdditiveBlending });
  const glow = new THREE.Sprite(glowMat);
  glow.scale.set(9, 9, 1);
  glow.position.set(0, 0, -1.5);
  scene.add(glow);

  // ---- Two interlocked wedding rings, PBR gold ----
  const ringGroup = new THREE.Group();
  const ringGeo = new THREE.TorusGeometry(1.85, 0.15, 64, 160);
  const ringMatA = new THREE.MeshPhysicalMaterial({
    color: GOLD, metalness: 1, roughness: 0.22, clearcoat: 0.6, clearcoatRoughness: 0.25,
    emissive: 0x3a2405, emissiveIntensity: 0.4,
  });
  const ringMatB = ringMatA.clone();

  const ring1 = new THREE.Mesh(ringGeo, ringMatA);
  ring1.position.x = -0.95;
  ring1.rotation.x = Math.PI / 2.35;

  const ring2 = new THREE.Mesh(ringGeo, ringMatB);
  ring2.position.x = 0.95;
  ring2.rotation.x = -Math.PI / 2.35;
  ring2.rotation.y = 0.3;

  ringGroup.add(ring1, ring2);
  ringGroup.scale.setScalar(1.12);
  scene.add(ringGroup);

  // ---- Drifting golden petals (soft additive sprites, not flat dots) ----
  function makePetalTexture() {
    const size = 128;
    const c = document.createElement('canvas');
    c.width = c.height = size;
    const ctx = c.getContext('2d');
    ctx.translate(size / 2, size / 2);
    const g = ctx.createRadialGradient(0, 0, 0, 0, 0, size / 2);
    g.addColorStop(0, 'rgba(255,244,214,0.95)');
    g.addColorStop(0.55, 'rgba(230,190,110,0.55)');
    g.addColorStop(1, 'rgba(230,190,110,0)');
    ctx.fillStyle = g;
    ctx.beginPath();
    ctx.ellipse(0, 0, size * 0.5, size * 0.32, 0, 0, Math.PI * 2);
    ctx.fill();
    return new THREE.CanvasTexture(c);
  }
  const petalTex = makePetalTexture();
  const PETAL_COUNT = 60;
  const petals = new THREE.Group();
  const petalData = [];
  for (let i = 0; i < PETAL_COUNT; i++) {
    const mat = new THREE.SpriteMaterial({
      map: petalTex, transparent: true, depthWrite: false, blending: THREE.AdditiveBlending,
      opacity: 0.35 + Math.random() * 0.4,
    });
    const sprite = new THREE.Sprite(mat);
    const scale = 0.18 + Math.random() * 0.3;
    sprite.scale.set(scale * 1.6, scale, 1);
    sprite.position.set((Math.random() - 0.5) * 14, (Math.random() - 0.5) * 9, (Math.random() - 0.5) * 6 - 1);
    petals.add(sprite);
    petalData.push({
      sprite,
      speed: 0.15 + Math.random() * 0.25,
      driftX: (Math.random() - 0.5) * 0.4,
      phase: Math.random() * Math.PI * 2,
      spin: (Math.random() - 0.5) * 0.6,
    });
  }
  scene.add(petals);

  // ---- Pointer parallax + scroll reactivity ----
  let targetRotX = 0, targetRotY = 0;
  window.addEventListener('pointermove', (e) => {
    targetRotY = (e.clientX / window.innerWidth - 0.5) * 0.6;
    targetRotX = (e.clientY / window.innerHeight - 0.5) * 0.4;
  });

  const heroSection = document.querySelector('.hero');
  let scrollProgress = 0;
  const updateScrollProgress = () => {
    if (!heroSection) return;
    const rect = heroSection.getBoundingClientRect();
    scrollProgress = Math.min(1, Math.max(0, -rect.top / Math.max(rect.height, 1)));
  };
  window.addEventListener('scroll', updateScrollProgress, { passive: true });
  updateScrollProgress();

  const clock = new THREE.Clock();
  function animate() {
    const t = clock.getElapsedTime();

    ringGroup.rotation.y += 0.0032;
    ringGroup.rotation.z = Math.sin(t * 0.3) * 0.08;
    ringGroup.rotation.x += (targetRotX - ringGroup.rotation.x) * 0.03;
    ringGroup.position.y = Math.sin(t * 0.6) * 0.16 - scrollProgress * 1.8;

    glow.material.rotation = t * 0.05;
    glow.position.y = ringGroup.position.y;

    petalData.forEach(({ sprite, speed, driftX, phase, spin }) => {
      sprite.position.y += Math.sin(t * speed + phase) * 0.0015 - 0.0012;
      sprite.position.x += driftX * 0.002;
      sprite.material.rotation += spin * 0.004;
      if (sprite.position.y < -5) sprite.position.y = 5;
      if (sprite.position.x > 8) sprite.position.x = -8;
      if (sprite.position.x < -8) sprite.position.x = 8;
    });

    camera.position.z = 9.5 + scrollProgress * 2.5;
    camera.position.x += (targetRotY * 0.8 - camera.position.x) * 0.02;
    camera.lookAt(0, ringGroup.position.y * 0.3, 0);

    renderer.render(scene, camera);
    requestAnimationFrame(animate);
  }
  animate();
}
