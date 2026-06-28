import { readFile } from 'node:fs/promises'
import { fileURLToPath } from 'node:url'
import { dirname, resolve } from 'node:path'
import { describe, expect, test } from 'vitest'

const projectRoot = resolve(dirname(fileURLToPath(import.meta.url)), '..')

const readProjectFile = (path) => readFile(resolve(projectRoot, path), 'utf8')

describe('frontend scaffold contract', () => {
  test('defines the expected app entry point and source alias', async () => {
    const [indexHtml, viteConfig] = await Promise.all([
      readProjectFile('index.html'),
      readProjectFile('vite.config.js')
    ])

    expect(indexHtml).toContain('<div id="app"></div>')
    expect(indexHtml).toContain('src="/src/main.js"')
    expect(viteConfig).toContain("'@': fileURLToPath(new URL('./src', import.meta.url))")
  })
})
