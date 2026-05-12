import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import tailwindcss from "@tailwindcss/vite";

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), tailwindcss()],
  build: {
    outDir: "dist",
    sourcemap: false,
    minify: "esbuild",
    rollupOptions: {
      output: {
        manualChunks: {
          vendor: ["react", "react-dom"],
          redux: ["@reduxjs/toolkit", "react-redux"],
          router: ["react-router-dom"],
          ui: [
            "@fortawesome/react-fontawesome",
            "@fortawesome/fontawesome-svg-core",
          ],
        },
      },
    },
  },
  base: "/",
  server: {
    port: 5173,
    proxy: {
      // Proxy API requests to the backend during development.
      // Adjust the target if your Spring Boot app runs on a different port.
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
        secure: false,
      },
    },
  },
  preview: { port: 5173 },
});
