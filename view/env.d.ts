declare module 'process' {
  global {
    namespace NodeJS {
      interface ProcessEnv {
        API_URL: string
        STORAGE_URL: string
      }
    }
  }
}
