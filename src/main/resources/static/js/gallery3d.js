// Shaadi Metrics — 3D photo-ring showcase. Real wedding photos on gold-framed
// planes arranged in a circle; drag to spin, click a photo to open it full-size.

import * as THREE from 'three';

const canvas = document.getElementById('gallery3d-canvas');
if (canvas) {
  const stage = canvas.parentElement;
  const images = (window.__featuredGalleryImages && window.__featuredGalleryImages.length)
    ? window.__featuredGalleryImages
    : [
        '/images/gallery/mandap/01.jpeg', '/images/gallery/sangeet/01.jpeg', '/images/gallery/reception/01.jpeg',
        '/images/gallery/haldi/01.jpeg', '/images/gallery/baraat/01.jpg', '/images/gallery/entry/01.jpeg',
      ];

  const scene = new THREE.Scene();
  const camera = new THREE.PerspectiveCamera(45, canvas.clientWidth / canvas.clientHeight, 0.1, 100);

  const renderer = new THREE.WebGLRenderer({ canvas, alpha: true, antialias: true });
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
  renderer.outputColorSpace = THREE.SRGBColorSpace;

  const resize = () => {
    const { clientWidth, clientHeight } = canvas;
    if (clientWidth === 0 || clientHeight === 0) return;
    renderer.setSize(clientWidth, clientHeight, false);
    camera.aspect = clientWidth / clientHeight;
    camera.updateProjectionMatrix();
  };
  resize();
  window.addEventListener('resize', resize);

  scene.add(new THREE.AmbientLight(0xfff3d6, 1.4));
  const key = new THREE.PointLight(0xf3dd9a, 45, 40, 2);
  key.position.set(3, 4, 6);
  scene.add(key);
  const rim = new THREE.PointLight(0x9fc2ff, 12, 30, 2);
  rim.position.set(-5, -2, 3);
  scene.add(rim);

  const ringGroup = new THREE.Group();
  scene.add(ringGroup);

  const count = images.length;
  const radius = 3.4 + count * 0.22;
  camera.position.set(0, 0.4, radius + 6.5);
  const loader = new THREE.TextureLoader();
  const frameMat = new THREE.MeshStandardMaterial({ color: 0xe8c15e, metalness: 0.55, roughness: 0.3, emissive: 0x6b4a10, emissiveIntensity: 0.55 });
  const frameGeo = new THREE.PlaneGeometry(2.5, 3.2);
  const photoGeo = new THREE.PlaneGeometry(2.28, 2.98);

  const items = [];
  images.forEach((url, i) => {
    const angle = (i / count) * Math.PI * 2;
    const pivot = new THREE.Group();
    pivot.position.set(radius * Math.sin(angle), 0, radius * Math.cos(angle));
    pivot.rotation.y = angle;

    const frame = new THREE.Mesh(frameGeo, frameMat);
    frame.position.z = -0.06;
    pivot.add(frame);

    const texture = loader.load(url);
    texture.colorSpace = THREE.SRGBColorSpace;
    const photoMat = new THREE.MeshBasicMaterial({ map: texture, transparent: true });
    const photo = new THREE.Mesh(photoGeo, photoMat);
    photo.userData.url = url;
    pivot.add(photo);

    ringGroup.add(pivot);
    items.push({ pivot, photo, angle, baseY: 0 });
  });

  // ---- Drag to rotate, with momentum + idle auto-rotate ----
  let isDragging = false;
  let lastX = 0;
  let velocity = 0.0015;
  let lastInteraction = 0;

  const onDown = (clientX) => { isDragging = true; lastX = clientX; velocity = 0; lastInteraction = performance.now(); };
  const onMove = (clientX) => {
    if (!isDragging) return;
    const delta = clientX - lastX;
    ringGroup.rotation.y += delta * 0.0055;
    velocity = delta * 0.00025;
    lastX = clientX;
    lastInteraction = performance.now();
  };
  const onUp = () => { isDragging = false; };

  canvas.addEventListener('pointerdown', (e) => { onDown(e.clientX); canvas.setPointerCapture(e.pointerId); });
  canvas.addEventListener('pointermove', (e) => onMove(e.clientX));
  canvas.addEventListener('pointerup', onUp);
  canvas.addEventListener('pointerleave', onUp);

  // ---- Click a photo to open it in the site's shared lightbox ----
  const raycaster = new THREE.Raycaster();
  const pointer = new THREE.Vector2();
  let downX = 0, downY = 0;
  canvas.addEventListener('pointerdown', (e) => { downX = e.clientX; downY = e.clientY; });
  canvas.addEventListener('pointerup', (e) => {
    if (Math.hypot(e.clientX - downX, e.clientY - downY) > 6) return; // it was a drag, not a click
    const rect = canvas.getBoundingClientRect();
    pointer.x = ((e.clientX - rect.left) / rect.width) * 2 - 1;
    pointer.y = -((e.clientY - rect.top) / rect.height) * 2 + 1;
    raycaster.setFromCamera(pointer, camera);
    const hits = raycaster.intersectObjects(items.map((it) => it.photo));
    if (hits.length > 0) {
      const url = hits[0].object.userData.url;
      const lightbox = document.querySelector('.lightbox');
      if (lightbox) {
        lightbox.querySelector('img').src = url;
        lightbox.classList.add('open');
      }
    }
  });

  const clock = new THREE.Clock();
  function animate() {
    const t = clock.getElapsedTime();
    const idle = performance.now() - lastInteraction > 1200;

    if (!isDragging) {
      if (idle) velocity += (0.0012 - velocity) * 0.01;
      ringGroup.rotation.y += velocity;
      velocity *= 0.985;
    }

    items.forEach(({ pivot }, i) => {
      pivot.position.y = Math.sin(t * 0.5 + i) * 0.08;
    });

    renderer.render(scene, camera);
    requestAnimationFrame(animate);
  }
  animate();
}
