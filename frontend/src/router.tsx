// src/router.tsx
// Central route table for SPA & fallback handling
import { createBrowserRouter } from 'react-router-dom'
import App from './App'

// * Router that funnels all paths to App
export const router = createBrowserRouter([
  {
    path: '/*',
    element: <App />,
  },
])
