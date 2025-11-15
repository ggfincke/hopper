// src/main.tsx
// Bootstraps root render & wraps App w/ StrictMode
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App'

// mount App tree inside #root container
createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
