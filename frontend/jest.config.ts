import {Config} from 'jest';

const jestConfig: Config = {

  preset: 'jest-preset-angular',
  setupFilesAfterEnv: ['<rootDir>/setup-jest.ts'],
  globalSetup: 'jest-preset-angular/global-setup',
  reporters: [
    'default',
  ],
  collectCoverageFrom: ['src/**/*.ts', '!**/node_modules/**'],
  coverageDirectory: 'coverage',
  coverageReporters: ['cobertura', 'text', 'text-summary', 'html'],
  cacheDirectory: '.jestcache'
};

export default jestConfig;
