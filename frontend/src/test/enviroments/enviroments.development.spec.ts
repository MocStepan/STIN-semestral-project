import {environment} from "../../environments/environment.dev";

describe('Development Environment Configuration', () => {
  it('should have correct baseUrl for development', () => {
    expect(environment.baseUrl).toBe('http://localhost:8080/');
  });

  it('should have production set to false for development', () => {
    expect(environment.production).toBeFalsy();
  });

  it('should have version set to 0.0.1 for development', () => {
    expect(environment.version).toBe('0.0.1');
  });
});
