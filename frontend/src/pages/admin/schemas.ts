import { z } from "zod";

export const movieFormSchema = z.object({
  title: z.string().trim().min(1, "Titulo e obrigatorio").max(255, "Titulo muito longo"),
  description: z.string().max(5000, "Descricao muito longa").optional().or(z.literal("")),
  genre: z.string().max(100, "Genero muito longo").optional().or(z.literal("")),
  director: z.string().max(255, "Diretor muito longo").optional().or(z.literal("")),
  movieType: z.string().max(50, "Tipo muito longo").optional().or(z.literal("")),
  releaseDate: z
    .string()
    .regex(/^$|^\d{4}-\d{2}-\d{2}$/, "Use o formato YYYY-MM-DD")
    .optional(),
  durationMinutes: z.coerce.number().int("Duracao invalida").positive("Duracao deve ser positiva"),
  price: z.coerce.number().positive("Preco deve ser maior que zero").max(999.99, "Preco maximo: 999.99"),
  imageUrl: z
    .string()
    .regex(/^$|^https?:\/\/.+$/, "URL precisa iniciar com http:// ou https://")
    .optional(),
  available: z.boolean().default(true)
});

export const categoryFormSchema = z.object({
  name: z.string().trim().min(1, "Nome e obrigatorio").max(100, "Nome muito longo"),
  description: z.string().max(500, "Descricao muito longa").optional().or(z.literal(""))
});

export type MovieFormValues = z.infer<typeof movieFormSchema>;
export type CategoryFormValues = z.infer<typeof categoryFormSchema>;

