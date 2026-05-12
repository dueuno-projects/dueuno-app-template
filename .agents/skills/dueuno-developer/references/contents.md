# Contents

Contents are the canvas to each feature. You can create a ContentBlank, which is a plain empty canvas, and add Components to it. This is not something you will usually want to do since Dueuno provides pre-assembled contents to be used right away.

Components are added to the content on a vertical stripe one after the other. We can not layout components, to create a layout we need to use the Form component or we can create a custom component.

`ContentBase`
Embeds a Header and a Confirm Button that submits a component called form (not provided) to an action called onConfirm.

`ContentForm`
Extends ContentHeader and embeds a Form called form.

`ContentCreate`
Extends ContentForm and provides a Create Button that submits the form component to an action called onCreate.

`ContentEdit`
Extends ContentForm and provides a Save Button that submits the form component to an action called onEdit.

`ContentTable`
Extends ContentHeader and embeds a Table component. Provides a New Button that redirects to an action called create.

The Table component is configured to present and Edit and a Delete Button for each displayed row. The Edit Button submits the raw id to an action called edit while the Delete Button asks for confirmation before redirecting to an action called onDelete.
