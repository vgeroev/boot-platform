class LatexEditorService {
    private static readonly LS_KEY_LATEX = "tex4ht_editor_latex";
    private static readonly LS_KEY_CONFIGURATION = "tex4ht_editor_configuration";

    public saveLatex(input: string): void {
        localStorage.setItem(LatexEditorService.LS_KEY_LATEX, input);
    }

    public getLatex(): string | null {
        return localStorage.getItem(LatexEditorService.LS_KEY_LATEX);
    }

    public saveConfiguration(input: string): void {
        localStorage.setItem(LatexEditorService.LS_KEY_CONFIGURATION, input);
    }

    public getConfiguration(): string | null {
        return localStorage.getItem(LatexEditorService.LS_KEY_CONFIGURATION);
    }

    public clearUserInput(): void {
        localStorage.removeItem(LatexEditorService.LS_KEY_LATEX);
        localStorage.removeItem(LatexEditorService.LS_KEY_CONFIGURATION);
    }
}

const latexEditorService = new LatexEditorService();
export { latexEditorService };
