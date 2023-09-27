import { defineConfig } from 'cypress';

export const defaultConfig = {
  video: false,
  fixturesFolder: 'src/test/javascript/cypress/fixtures',
  screenshotsFolder: 'target/cypress/screenshots',
  downloadsFolder: 'target/cypress/downloads',
  videosFolder: 'target/cypress/videos',
  chromeWebSecurity: true,
  viewportWidth: 1200,
  viewportHeight: 720,
  retries: 0,
  env: {
    authenticationUrl: '/api/authenticate',
    jwtStorageName: 'jhi-authenticationToken',
  },
  e2e: {
    // We've imported your old cypress plugins here.
    // You may want to clean this up later by importing these.
    async setupNodeEvents(on, config) {
      return (await import('./src/test/javascript/cypress/plugins/index')).default(on, config);
    },
    baseUrl: 'http://localhost:8080/',
    specPattern: 'src/test/javascript/cypress/integration/**/*.spec.ts',
    supportFile: 'src/test/javascript/cypress/support/index.ts',
    experimentalSessionAndOrigin: true,
  },
};

export default defineConfig(defaultConfig);
