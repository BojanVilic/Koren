@file:OptIn(ExperimentalMaterial3Api::class)

package com.koren.home.ui.prototypes.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.koren.designsystem.theme.KorenTheme // Assuming KorenTheme is defined
import com.koren.designsystem.theme.ThemePreview // Assuming ThemePreview is defined

// --- Data Models (Firebase-friendly structure) ---

// Represents a task or chore
data class Task(
    val id: String? = null, // Firebase key
    val title: String = "",
    val description: String? = null,
    val assigneeUserId: String? = null, // User assigned to the task
    val dueTimestamp: Long? = null, // Due date/time
    val isCompleted: Boolean = false,
    val completedByUserId: String? = null,
    val completedTimestamp: Long? = null,
    val isRecurring: Boolean = false,
    val recurrenceRule: String? = null, // e.g., "FREQ=WEEKLY;BYDAY=MO,WE,FR" (iCalendar format simplified)
    val pointsValue: Int = 0, // Points awarded for completion
    val parentTaskId: String? = null // For subtasks
)

// Represents a user's points balance (simplified)
data class UserPoints(
    val userId: String? = null, // Firebase key
    val points: Int = 0
)


// --- Composable Prototypes ---

/**
 * Prototype screen for creating/editing a Task with recurrence and points options, enhanced styling.
 */
@Composable
fun TaskEditScreenPrototype(
    task: Task,
    onSaveTask: (Task) -> Unit,
    onCancel: () -> Unit,
    getUserName: (String) -> String = { "User $it" } // Placeholder
) {
    var editedTask by remember { mutableStateOf(task) }
    // State for recurrence options visibility, date/time pickers, assignee picker, etc.

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (task.id == null) "Create Task" else "Edit Task") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel")
                    }
                },
                actions = {
                    Button(onClick = { onSaveTask(editedTask) }) {
                        Text("Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = editedTask.title,
                onValueChange = { editedTask = editedTask.copy(title = it) },
                label = { Text("Task Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = editedTask.description ?: "",
                onValueChange = { editedTask = editedTask.copy(description = it.takeIf { it.isNotBlank() }) },
                label = { Text("Description (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = editedTask.isRecurring,
                            onCheckedChange = { editedTask = editedTask.copy(isRecurring = it) }
                        )
                        Text("Recurring Task", style = MaterialTheme.typography.bodyMedium)
                    }
                    if (editedTask.isRecurring) {
                        // Simple placeholder for recurrence rule UI
                        Text("Recurrence: ${editedTask.recurrenceRule ?: "Not set"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        // TODO: Add UI for setting recurrence rule (e.g., daily, weekly, specific days)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Due Date & Time", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(8.dp))
                    // TODO: Add Date and Time picker UI
                    Text(
                        "Selected: ${editedTask.dueTimestamp?.let { java.text.SimpleDateFormat("MMM dd,yyyy HH:mm").format(java.util.Date(it)) } ?: "Not set"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(16.dp))


            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Assignee", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Placeholder for user profile picture
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "Assignee", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(getUserName(editedTask.assigneeUserId ?: "unassigned"), style = MaterialTheme.typography.bodySmall) // Replace with user name
                        // TODO: Add User picker UI (e.g., dropdown or dialog)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = editedTask.pointsValue.toString(),
                        onValueChange = {
                            editedTask = editedTask.copy(pointsValue = it.toIntOrNull() ?: 0)
                        },
                        label = { Text("Points for Completion") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = { Icon(Icons.Default.Star, contentDescription = "Points") }
                    )
                }
            }
            Spacer(Modifier.height(16.dp))


            // TODO: Add UI for Subtasks if parentTaskId is used
        }
    }
}

/**
 * Prototype list item for a Task, showing completion and points, with enhanced styling.
 */
@Composable
fun TaskListItemPrototype(
    task: Task,
    onTaskCompletedToggle: (Task, Boolean) -> Unit,
    getUserName: (String) -> String = { "User $it" } // Placeholder
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { /* TODO: Open task details/edit screen */ }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text(task.title, style = MaterialTheme.typography.titleSmall)
                task.description?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                task.dueTimestamp?.let {
                    Text(
                        text = "Due: ${java.text.SimpleDateFormat("MMM dd,yyyy HH:mm").format(java.util.Date(it))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (task.dueTimestamp < System.currentTimeMillis() && !task.isCompleted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant // Highlight overdue tasks
                    )
                }
                task.assigneeUserId?.let {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Person, contentDescription = "Assignee", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(getUserName(it), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) // Replace with user name
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                if (task.pointsValue > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Star, contentDescription = "Points", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                        Text("${task.pointsValue}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    }
                }
                Spacer(Modifier.height(4.dp))
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { isChecked -> onTaskCompletedToggle(task, isChecked) }
                )
            }
        }
    }
}

/**
 * Prototype screen showing a list of tasks and user points, with enhanced styling.
 */
@Composable
fun TaskListScreenPrototype(
    tasks: List<Task>,
    userPoints: UserPoints?,
    onTaskCompletedToggle: (Task, Boolean) -> Unit,
    onAddTaskClick: () -> Unit,
    getUserName: (String) -> String = { "User $it" } // Placeholder
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Family Tasks & Chores") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTaskClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (userPoints != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Your Points:", style = MaterialTheme.typography.titleMedium)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.Star, contentDescription = "Points", tint = MaterialTheme.colorScheme.primary)
                                Text("${userPoints.points}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
            item { Text("Tasks", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp)) }
            items(tasks) { task ->
                TaskListItemPrototype(task = task, onTaskCompletedToggle = onTaskCompletedToggle, getUserName = getUserName)
            }
        }
    }
}


// --- Previews ---

@ThemePreview
@Composable
fun PreviewTaskEditScreen() {
    KorenTheme {
        Surface {
            TaskEditScreenPrototype(
                task = Task(
                    id = "1",
                    title = "Clean Room",
                    description = "Make bed, vacuum, put away clothes",
                    isRecurring = true,
                    recurrenceRule = "Weekly",
                    pointsValue = 50,
                    assigneeUserId = "userA"
                ),
                onSaveTask = {},
                onCancel = {}
            )
        }
    }
}

@ThemePreview
@Composable
fun PreviewTaskListScreen() {
    KorenTheme {
        Surface {
            TaskListScreenPrototype(
                tasks = listOf(
                    Task(id = "1", title = "Feed the dog", isCompleted = true, pointsValue = 10, assigneeUserId = "userA"),
                    Task(id = "2", title = "Take out trash", dueTimestamp = System.currentTimeMillis() - 3600000, pointsValue = 20, assigneeUserId = "userB"), // Overdue task
                    Task(id = "3", title = "Grocery Shopping", assigneeUserId = "userB", dueTimestamp = System.currentTimeMillis() + 86400000, pointsValue = 100)
                ),
                userPoints = UserPoints(userId = "userA", points = 150),
                onTaskCompletedToggle = { _, _ -> },
                onAddTaskClick = {}
            )
        }
    }
}