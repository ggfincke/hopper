// vite.config.ts
// Vite setup enabling React plugin & Tailwind preset
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

// * configure dev server plugins
export default defineConfig({
  plugins: [
    react(),
    tailwindcss(),
  ],
})
